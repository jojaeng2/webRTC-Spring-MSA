package webrtc.chatservice.service.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.controller.HttpApiController;
import webrtc.chatservice.domain.*;
import webrtc.chatservice.dto.ChannelDto.CreateChannelRequest;
import webrtc.chatservice.dto.chat.ChattingMessage;
import webrtc.chatservice.exception.ChannelException.*;
import webrtc.chatservice.exception.HashTagException.NotExistHashTagException;
import webrtc.chatservice.exception.UserException.NotExistUserException;
import webrtc.chatservice.repository.channel.ChannelCrudRepository;
import webrtc.chatservice.repository.channel.ChannelListRepository;
import webrtc.chatservice.repository.channel.ChannelRedisRepository;
import webrtc.chatservice.repository.hashtag.ChannelHashTagRepository;
import webrtc.chatservice.repository.hashtag.HashTagRepository;
import webrtc.chatservice.repository.users.ChannelUserRepository;
import webrtc.chatservice.repository.users.UsersRepository;
import webrtc.chatservice.service.chat.factory.ChattingMessageFactory;
import webrtc.chatservice.service.rabbit.RabbitPublish;
import java.util.List;
import java.util.Optional;

import static webrtc.chatservice.enums.ClientMessageType.CREATE;


@RequiredArgsConstructor
@Service
public class ChannelLifeServiceImpl implements ChannelLifeService {

    private final ChannelCrudRepository channelCrudRepository;
    private final ChannelHashTagRepository channelHashTagRepository;
    private final ChannelRedisRepository channelRedisRepository;
    private final ChannelUserRepository channelUserRepository;
    private final UsersRepository usersRepository;
    private final HashTagRepository hashTagRepository;
    private final RabbitPublish rabbitPublish;
    private final ChattingMessageFactory chattingMessageFactory;


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
        Channel channel = createChannelIfNotExist(request);
        channelCrudRepository.save(channel);

        for (String tagName : request.getHashTags()) {
            HashTag hashTag = findHashTag(tagName);
            hashTagRepository.save(hashTag);
            createChannelHashTag(channel, hashTag);
        }

        channelRedisRepository.createChannel(channel);
        Users user = pointDecreaseAndReturnUser(email);

        // 채널유저 생성
        createChannelUser(user, channel);

        // 채팅방 생성 로그
        createChatLog(channel, user);

        channelCrudRepository.save(channel);

        // rabbitMQ 메시지 전송
//        sendRabbitMessage(channel, user);
        return channel;
    }

    /*
     * 비즈니스 로직 - 채널 삭제
     *
     */
    @Transactional
    public void deleteChannel(String channelId) {
        Channel channel = channelCrudRepository.findById(channelId).orElseThrow(NotExistChannelException::new);
        channelCrudRepository.delete(channel);
        channelRedisRepository.delete(channelId);
    }

    @Transactional
    public void extensionChannelTTL(String channelId, String userEmail, Long requestTTL) {
        Channel channel = channelCrudRepository.findById(channelId).orElseThrow(NotExistChannelException::new);
        httpApiController.postDecreaseUserPoint(userEmail, requestTTL * pointUnit, userEmail + "님이 채널 연장에 포인트를 사용했습니다.");
        channelRedisRepository.extensionChannelTTL(channel, requestTTL * channelExtensionMinute * 60L);
    }

    private Channel createChannelIfNotExist(CreateChannelRequest request) {

        channelCrudRepository.findByChannelName(request.getChannelName())
                .ifPresent(channel -> { throw new AlreadyExistChannelException(); });

        return new Channel(request.getChannelName(), request.getChannelType());
    }

    private Users pointDecreaseAndReturnUser(String email) {

        httpApiController.postDecreaseUserPoint(email, channelCreatePoint * pointUnit, email + "님이 채널 생성에 포인트를 사용했습니다.");
        Users user = usersRepository.findUserByEmail(email)
                .orElse(httpApiController.postFindUserByEmail(email));
        usersRepository.save(user);
        return user;

    }

    private HashTag findHashTag(String tagName) {
        return hashTagRepository.findHashTagByName(tagName)
                .orElse(new HashTag(tagName));
    }

    private void createChannelUser(Users user, Channel channel) {
        ChannelUser channelUser = new ChannelUser(user, channel);
        channelUserRepository.save(channelUser);
    }

    private void createChannelHashTag(Channel channel, HashTag hashTag) {
        ChannelHashTag channelHashTag = new ChannelHashTag(channel, hashTag);
        channelHashTagRepository.save(channelHashTag);
    }

    private void createChatLog(Channel channel, Users user) {
        ChatLog chatLog = new ChatLog(CREATE, "[알림] " + user.getNickname() + "님이 채팅방을 생성했습니다.", user.getNickname(), "NOTICE");
        channel.addChatLog(chatLog);
    }

    private void sendRabbitMessage(Channel channel, Users user) {
        ChattingMessage serverMessage = chattingMessageFactory.createMessage(channel.getId(), CREATE, user.getNickname(), "[알림] " + user.getNickname() + "님이 채팅방을 생성했습니다.", 1L, List.of(user), 1L, user.getEmail());
        rabbitPublish.publishMessage(serverMessage, CREATE);
    }
}
