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
import webrtc.chatservice.repository.hashtag.HashTagRepository;
import webrtc.chatservice.repository.users.UsersRepository;
import webrtc.chatservice.service.chat.factory.ChattingMessageFactory;
import webrtc.chatservice.service.rabbit.RabbitPublish;
import java.util.List;
import java.util.Optional;

import static webrtc.chatservice.enums.ClientMessageType.CREATE;


@RequiredArgsConstructor
@Service
public class ChannelLifeServiceImpl implements ChannelLifeService {

    private final ChannelListRepository channelListRepository;
    private final ChannelCrudRepository channelCrudRepository;
    private final ChannelRedisRepository channelRedisRepository;
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
        Users user = pointDecreaseAndReturnUser(email);

        for (String tagName : request.getHashTags()) {
            HashTag hashTag = findHashTag(tagName);
            createChannelHashTag(channel, hashTag);
        }

        channelRedisRepository.createChannel(channel);
        createChannelUser(user, channel);

        // 채팅방 생성 로그
        ChatLog chatLog = new ChatLog(CREATE, "[알림] " + user.getNickname() + "님이 채팅방을 생성했습니다.", user.getNickname(), "NOTICE");
        channel.addChatLog(chatLog);
        channelCrudRepository.save(channel);


        // rabbitMQ 전용 메시지 생성
        ChattingMessage serverMessage = chattingMessageFactory.createMessage(channel.getId(), CREATE, user.getNickname(), "[알림] " + user.getNickname() + "님이 채팅방을 생성했습니다.", 1L, List.of(user), 1L, user.getEmail());
        rabbitPublish.publishMessage(serverMessage, CREATE);

        return channel;
    }

    private Channel createChannelIfNotExist(CreateChannelRequest request) {
        boolean exist = channelListRepository.findChannelByChannelName(request.getChannelName()).isEmpty();

        // 만약 채널이 존재하면 예외 터뜨리고, 없다면 새로 생성
        if(exist) {
            throw new AlreadyExistChannelException();
        }
        return new Channel(request.getChannelName(), request.getChannelType());
    }

    private Users pointDecreaseAndReturnUser(String email) {
        httpApiController.postDecreaseUserPoint(email, channelCreatePoint * pointUnit);
        Users user = usersRepository.findUserByEmail(email)
                .orElse(httpApiController.postFindUserByEmail(email));
        usersRepository.save(user);
        return user;

    }

    private HashTag findHashTag(String tagName) {
        return hashTagRepository.findHashTagByName(tagName)
                .orElse(new HashTag(tagName));
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
