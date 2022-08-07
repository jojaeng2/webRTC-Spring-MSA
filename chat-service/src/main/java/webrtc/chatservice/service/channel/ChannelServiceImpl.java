package webrtc.chatservice.service.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.controller.HttpApiController;
import webrtc.chatservice.domain.*;
import webrtc.chatservice.dto.ChannelDto.ChannelResponse;
import webrtc.chatservice.dto.ChannelDto.CreateChannelRequest;
import webrtc.chatservice.exception.ChannelException.*;
import webrtc.chatservice.exception.HashTagException.NotExistHashTagException;
import webrtc.chatservice.exception.UserException.NotExistUserException;
import webrtc.chatservice.repository.channel.ChannelDBRepository;
import webrtc.chatservice.repository.channel.ChannelHashTagRepository;
import webrtc.chatservice.repository.channel.ChannelRedisRepository;
import webrtc.chatservice.repository.channel.ChannelUserRepository;
import webrtc.chatservice.repository.chat.ChatLogRepository;
import webrtc.chatservice.repository.hashtag.HashTagRepository;
import webrtc.chatservice.repository.users.UsersRepository;

import java.util.ArrayList;
import java.util.List;

import static webrtc.chatservice.enums.ClientMessageType.CREATE;


@RequiredArgsConstructor
@Service
public class ChannelServiceImpl implements ChannelService{

    private final ChannelDBRepository channelDBRepository;
    private final ChannelRedisRepository channelRedisRepository;
    private final ChatLogRepository chatLogRepository;
    private final ChannelHashTagRepository channelHashTagRepository;
    private final UsersRepository usersRepository;
    private final ChannelUserRepository channelUserRepository;
    private final HashTagRepository hashTagRepository;

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
        Users users;
        try {
            channel = channelDBRepository.findChannelByChannelName(request.getChannelName());
            throw new AlreadyExistChannelException();
        } catch (NotExistChannelException ex1) {
            channel = new Channel(request.getChannelName(), request.getChannelType());
            try {
                users = usersRepository.findUserByEmail(email);
            } catch (NotExistUserException ex2) {
                users = httpApiController.postFindUserByEmail(email);
                usersRepository.saveUser(users);
            }
            httpApiController.postDecreaseUserPoint(users.getEmail(), channelCreatePoint * pointUnit);
        }

        List<String> hashTags = request.getHashTags();
        List<ChannelHashTag> channelHashTagList = new ArrayList<>();
        for (String tagName : hashTags) {
            HashTag hashTag;
            try {
                hashTag = hashTagRepository.findHashTagByName(tagName);
            } catch (NotExistHashTagException e) {
                hashTag = new HashTag(tagName);
            }

            ChannelHashTag channelHashTag = new ChannelHashTag(channel, hashTag);
            channelHashTagList.add(channelHashTag);
            hashTag.addChannelHashTag(channelHashTag);
            channel.addChannelHashTag(channelHashTag);
            channelHashTagRepository.save(channelHashTag);
        }

        channelDBRepository.createChannel(channel, channelHashTagList);
        channelRedisRepository.createChannel(channel);
        createChannelUser(users, channel);

        List<ChatLog> findChatLogs = chatLogRepository.findLastChatLogsByChannelId(channel.getId());
        ChatLog chatLog = new ChatLog(CREATE, "[알림] " + users.getNickname() + "님이 채팅방을 생성했습니다.", users.getNickname(), "NOTICE");
        if(findChatLogs.isEmpty()) chatLog.setChatLogIdx(1L);
        else chatLog.setChatLogIdx(findChatLogs.get(0).getIdx()+1);
        chatLog.setChannel(channel);
        chatLogRepository.save(chatLog);
        return channel;
    }

    /*
     * 비즈니스 로직 - 채널 입장
     *
     */
    @Transactional
    public void enterChannel(Channel channel, String email) {
        Users users;

        // 무조건 users 객체 가져와야함
        try {
            users = usersRepository.findUserByEmail(email);
        } catch (NotExistUserException e) {
            users = httpApiController.postFindUserByEmail(email);
            usersRepository.saveUser(users);
        }


        String channelId = channel.getId();
        try {
            channelDBRepository.findChannelsByChannelIdAndUserId(channelId, users.getId());
            throw new AlreadyExistUserInChannelException();
        } catch (NotExistChannelException e) {
            Long limitParticipants = channel.getLimitParticipants();
            Long currentParticipants = channel.getCurrentParticipants();
            if(limitParticipants.equals(currentParticipants)) throw new ChannelParticipantsFullException();
            else {
                createChannelUser(users, channel);
            }
        }
    }

    /*
     * 비즈니스 로직 - 채널 퇴장
     *
     */
    @Transactional
    public void exitChannel(String channelId, String userId) {
        Channel channel = findOneChannelById(channelId);
        ChannelUser channelUser = channelUserRepository.findOneChannelUser(channelId, userId);
        channelDBRepository.exitChannelUserInChannel(channel, channelUser);
    }

    /*
     * 비즈니스 로직 - 채널 삭제
     *
     */
    @Transactional
    public void deleteChannel(String channelId) {
        Channel channel = findOneChannelById(channelId);
        channelDBRepository.deleteChannel(channel);
        channelRedisRepository.delete(channelId);
    }


    /*
     * 비즈니스 로직 - 모든 채널 불러오기
     *
     */
    @Transactional
    public List<ChannelResponse> findAnyChannel(String orderType, int idx) {
        switch (orderType) {
            case "partiASC" :
                return setReturnChannelsTTL(channelDBRepository.findAnyChannelByPartiASC(idx));
            case "partiDESC" :
                return setReturnChannelsTTL(channelDBRepository.findAnyChannelByPartiDESC(idx));
        }
        return new ArrayList<>();
    }

    /*
     * 비즈니스 로직 - 입장한 모든 채널 불러오기
     *
     */
    @Transactional
    public List<ChannelResponse> findMyChannel(String orderType, String email, int idx) {
        Users users = usersRepository.findUserByEmail(email);
        switch (orderType) {
            case "partiASC" :
                return setReturnChannelsTTL(channelDBRepository.findMyChannelByPartiASC(users.getId(), idx));
            case "partiDESC" :
                return setReturnChannelsTTL(channelDBRepository.findMyChannelByPartiDESC(users.getId(), idx));
        }
        return new ArrayList<>();
    }

    /*
     * 비즈니스 로직 - 특정 채널 ID로 찾기
     */
    @Transactional
    public Channel findOneChannelById(String channelId) {
        Channel channel = channelDBRepository.findChannelById(channelId);
        Long ttl = channelRedisRepository.findChannelTTL(channelId);
        channel.setTimeToLive(ttl);
        return channel;
    }

    @Transactional
    public List<ChannelResponse> findChannelByHashName(String tagName, String orderType, int idx) {
        HashTag hashTag = hashTagRepository.findHashTagByName(tagName);
        switch (orderType) {
            case "partiASC" :
                return setReturnChannelsTTL(channelDBRepository.findChannelsByHashNameAndPartiASC(hashTag, idx));
            case "partiDESC" :
                return setReturnChannelsTTL(channelDBRepository.findChannelsByHashNameAndPartiDESC(hashTag, idx));
        }
        return new ArrayList<>();
    }

    @Transactional
    public void extensionChannelTTL(String channelId, String userEmail, Long requestTTL) {
        Channel channel = channelDBRepository.findChannelById(channelId);
        httpApiController.postDecreaseUserPoint(userEmail, requestTTL * pointUnit);
        channelRedisRepository.extensionChannelTTL(channel, requestTTL * channelExtensionMinute * 60L);
    }

    private void createChannelUser(Users users, Channel channel) {
        ChannelUser channelUser = new ChannelUser(users, channel);
    }

    private List<ChannelResponse> setReturnChannelsTTL(List<Channel> channels) {
        List<ChannelResponse> responses = new ArrayList<>();
        for (Channel channel : channels) {
            Long ttl = channelRedisRepository.findChannelTTL(channel.getId());
            channel.setTimeToLive(ttl);
            ChannelResponse response = new ChannelResponse(channel.getId(), channel.getChannelName(), channel.getLimitParticipants(), channel.getCurrentParticipants(), channel.getTimeToLive(), channel.getChannelHashTags(), channel.getChannelType());
            responses.add(response);
        }
        return responses;
    }
}
