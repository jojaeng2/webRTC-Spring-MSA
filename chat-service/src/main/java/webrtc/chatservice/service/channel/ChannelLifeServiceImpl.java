package webrtc.chatservice.service.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.controller.HttpApiController;
import webrtc.chatservice.domain.*;
import webrtc.chatservice.dto.ChannelDto.CreateChannelRequest;
import webrtc.chatservice.dto.ChatDto.ChatServerMessage;
import webrtc.chatservice.enums.SocketServerMessageType;
import webrtc.chatservice.exception.ChannelException.*;
import webrtc.chatservice.exception.HashTagException.NotExistHashTagException;
import webrtc.chatservice.exception.UserException.NotExistUserException;
import webrtc.chatservice.repository.channel.ChannelDBRepository;
import webrtc.chatservice.repository.channel.ChannelRedisRepository;
import webrtc.chatservice.repository.channel.ChannelUserRepository;
import webrtc.chatservice.repository.hashtag.HashTagRepository;
import webrtc.chatservice.repository.users.UsersRepository;
import webrtc.chatservice.service.rabbit.RabbitPublish;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static webrtc.chatservice.enums.ClientMessageType.CREATE;


@RequiredArgsConstructor
@Service
public class ChannelLifeServiceImpl implements ChannelLifeService {

    private final ChannelDBRepository channelDBRepository;
    private final ChannelRedisRepository channelRedisRepository;
    private final UsersRepository usersRepository;
    private final HashTagRepository hashTagRepository;
    private final RabbitPublish rabbitPublish;


    // 30분당 100포인트
    private final Long pointUnit = 100L;
    private final Long channelCreatePoint = 2L;
    private final Long channelExtensionMinute = 30L;
    private final HttpApiController httpApiController;

    /**
     * 비즈니스 로직 - 채널 생성
     */
    @Transactional
    public Channel createChannel(CreateChannelRequest request, String email) {
        Channel channel;
        Users user;
        try {
            channel = channelDBRepository.findChannelByChannelName(request.getChannelName());
            throw new AlreadyExistChannelException();
        } catch (NotExistChannelException ex1) {
            channel = new Channel(request.getChannelName(), request.getChannelType());
            httpApiController.postDecreaseUserPoint(email, channelCreatePoint * pointUnit);
            try {
                user = usersRepository.findUserByEmail(email);
            } catch (NotExistUserException ex2) {
                user = httpApiController.postFindUserByEmail(email);
                usersRepository.saveUser(user);
            }
        }

        List<String> hashTags = request.getHashTags();

//                hashTags.stream()
//                        .map(tagName -> {
//                            HashTag hashTag;
//                            try {
//                                hashTag = hashTagRepository.findHashTagByName(tagName);
//                            } catch (NotExistHashTagException e) {
//                                hashTag = new HashTag(tagName);
//                            }
//                            ChannelHashTag channelHashTag = new ChannelHashTag(channel, hashTag);
//
//                            channel.addChannelHashTag(channelHashTag);
//                            hashTag.addChannelHashTag(channelHashTag);
//                            return channelHashTag;
//                        })
//                        .collect(Collectors.toList());


        for (String tagName : hashTags) {
            HashTag hashTag;
            try {
                hashTag = hashTagRepository.findHashTagByName(tagName);
            } catch (NotExistHashTagException e) {
                hashTag = new HashTag(tagName);
            }
            createChannelHashTag(channel, hashTag);
        }



        channelRedisRepository.createChannel(channel);
        createChannelUser(user, channel);

        // 채팅방 생성 로그
        ChatLog chatLog = new ChatLog(CREATE, "[알림] " + user.getNickname() + "님이 채팅방을 생성했습니다.", user.getNickname(), "NOTICE");
        chatLog.setChatLogIdx(1L);
        channel.addChatLog(chatLog);
        channelDBRepository.save(channel);


        // rabbitMQ 전용 메시지 생성
        ChatServerMessage serverMessage = new ChatServerMessage(channel.getId());
        List<Users> currentUsers = new ArrayList<>();
        currentUsers.add(user);
        serverMessage.setMessageType(SocketServerMessageType.CREATE, user.getNickname(),"[알림] " + user.getNickname() + "님이 채팅방을 생성했습니다.", channel.getCurrentParticipants(), currentUsers,user.getEmail());

        rabbitPublish.publishMessage(serverMessage, CREATE);


        return channel;
    }

    /*
     * 비즈니스 로직 - 채널 삭제
     *
     */
    @Transactional
    public void deleteChannel(String channelId) {
        Channel channel = channelDBRepository.findChannelById(channelId);
        channelDBRepository.deleteChannel(channel);
        channelRedisRepository.delete(channelId);
    }

    @Transactional
    public void extensionChannelTTL(String channelId, String userEmail, Long requestTTL) {
        Channel channel = channelDBRepository.findChannelById(channelId);
        httpApiController.postDecreaseUserPoint(userEmail, requestTTL * pointUnit);
        channelRedisRepository.extensionChannelTTL(channel, requestTTL * channelExtensionMinute * 60L);
    }

    private void createChannelUser(Users user, Channel channel) {
        new ChannelUser(user, channel);
    }

    private void createChannelHashTag(Channel channel, HashTag hashTag) {
        new ChannelHashTag(channel, hashTag);
    }
}
