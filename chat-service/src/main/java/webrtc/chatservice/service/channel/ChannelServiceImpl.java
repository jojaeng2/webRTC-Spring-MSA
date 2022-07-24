package webrtc.chatservice.service.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.controller.HttpApiController;
import webrtc.chatservice.controller.channel.api.ChannelApiController;
import webrtc.chatservice.domain.*;
import webrtc.chatservice.dto.ChannelDto.ChannelResponse;
import webrtc.chatservice.dto.ChannelDto.CreateChannelRequest;
import webrtc.chatservice.exception.ChannelException.*;
import webrtc.chatservice.exception.UserException.NotExistUserException;
import webrtc.chatservice.repository.channel.ChannelRepository;
import webrtc.chatservice.repository.channel.ChannelUserRepository;
import webrtc.chatservice.repository.chat.ChatLogRepository;
import webrtc.chatservice.repository.user.UserRepository;
import webrtc.chatservice.utils.CustomJsonMapper;

import java.util.ArrayList;
import java.util.List;

import static webrtc.chatservice.enums.ClientMessageType.CREATE;


@RequiredArgsConstructor
@Service
public class ChannelServiceImpl implements ChannelService{

    private final ChannelRepository channelRepository;
    private final ChatLogRepository chatLogRepository;
    private final UserRepository userRepository;
    private final ChannelUserRepository channelUserRepository;
    private final Long pointUnit = 100L;
    private final HttpApiController httpApiController;

    /**
     * 비즈니스 로직 - 채널 생성
     * @Param channelName
     * @Param limitParticipants
     *
     * @return Channel
     */
    @Transactional
    public Channel createChannel(CreateChannelRequest request, String email) {
        Channel channel;
        User user;
        try {
            channel = channelRepository.findChannelByChannelName(request.getChannelName());
            throw new AlreadyExistChannelException();
        } catch (NotExistChannelException ex1) {
            channel = new Channel(request.getChannelName(), request.getChannelType());
            try {
                user = userRepository.findUserByEmail(email);
            } catch (NotExistUserException ex2) {
                user = httpApiController.postFindUserByEmail(email);
                userRepository.saveUser(user);
            }
        }

        List<String> hashTags = request.getHashTags();
        channelRepository.createChannel(channel, hashTags);

        createChannelUser(user, channel);

        List<ChatLog> findChatLogs = chatLogRepository.findLastChatLogsByChannelId(channel.getId());
        ChatLog chatLog = new ChatLog(CREATE, "[알림] " + user.getNickname() + "님이 채팅방을 생성했습니다.", user.getNickname(), "NOTICE");
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
        User user;

        // 무조건 user 객체 가져와야함
        try {
            user = userRepository.findUserByEmail(email);
        } catch (NotExistUserException e) {
            user = httpApiController.postFindUserByEmail(email);
            userRepository.saveUser(user);
        }


        String channelId = channel.getId();
        try {
            channelRepository.findChannelsByChannelIdAndUserId(channelId, user.getId());
            throw new AlreadyExistUserInChannelException();

        } catch (NotExistChannelException e) {
            Long limitParticipants = channel.getLimitParticipants();
            Long currentParticipants = channel.getCurrentParticipants();
            if(limitParticipants.equals(currentParticipants)) throw new ChannelParticipantsFullException();
            else {
                createChannelUser(user, channel);
            }
        }
    }

    /*
     * 비즈니스 로직 - 채널 퇴장
     *
     */
    @Transactional
    public void exitChannel(String channelId, User user) {
        Channel channel = findOneChannelById(channelId);
        ChannelUser channelUser = channelUserRepository.findOneChannelUser(channelId, user.getId());
        channelRepository.exitChannelUserInChannel(channel, channelUser);
    }

    /*
     * 비즈니스 로직 - 채널 삭제
     *
     */
    @Transactional
    public void deleteChannel(String channelId) {
        Channel channel = findOneChannelById(channelId);
        channelRepository.deleteChannel(channel);
    }


    /*
     * 비즈니스 로직 - 모든 채널 불러오기
     *
     */
    @Transactional
    public List<ChannelResponse> findAnyChannel(String orderType, int idx) {
        switch (orderType) {
            case "partiASC" :
                return setReturnChannelsTTL(channelRepository.findAnyChannelByPartiASC(idx));
            case "partiDESC" :
                return setReturnChannelsTTL(channelRepository.findAnyChannelByPartiDESC(idx));
        }
        return new ArrayList<>();
    }

    /*
     * 비즈니스 로직 - 입장한 모든 채널 불러오기
     *
     */
    @Transactional
    public List<ChannelResponse> findMyChannel(String orderType, String email, int idx) {
        User user = userRepository.findUserByEmail(email);
        switch (orderType) {
            case "partiASC" :
                return setReturnChannelsTTL(channelRepository.findMyChannelByPartiASC(user.getId(), idx));
            case "partiDESC" :
                return setReturnChannelsTTL(channelRepository.findMyChannelByPartiDESC(user.getId(), idx));
        }
        return new ArrayList<>();
    }

    /*
     * 비즈니스 로직 - 특정 채널 ID로 찾기
     */
    @Transactional
    public Channel findOneChannelById(String channelId) {
        List<Channel> channels = channelRepository.findChannelsById(channelId);
        if(channels.size() == 0) throw new NotExistChannelException();
        Channel findChannel = channels.get(0);
        findChannel.setTimeToLive(channelRepository.findChannelTTL(channelId));
        return findChannel;
    }

    @Transactional
    public List<ChannelResponse> findChannelByHashName(String tagName, String orderType, int idx) {
        switch (orderType) {
            case "partiASC" :
                return setReturnChannelsTTL(channelRepository.findChannelsByHashNameAndPartiASC(tagName, idx));
            case "partiDESC" :
                return setReturnChannelsTTL(channelRepository.findChannelsByHashNameAndPartiDESC(tagName, idx));
        }
        return new ArrayList<>();
    }

    @Transactional
    public void extensionChannelTTL(String channelId, String userEmail, Long requestTTL) {
        List<Channel> channels = channelRepository.findChannelsById(channelId);
        if(channels.isEmpty()) throw new NotExistChannelException();
        Channel channel = channels.get(0);
        httpApiController.postDecreaseUserPoint(userEmail, requestTTL * pointUnit);
        channelRepository.extensionChannelTTL(channel, requestTTL * 30 * 60);
    }

    private void createChannelUser(User user, Channel channel) {
        ChannelUser channelUser = new ChannelUser(user, channel);
        channelUserRepository.save(channelUser);
//        userRepository.setChannelUser(user, channelUser);
        channelRepository.enterChannelUserInChannel(channel, channelUser);
    }

    private List<ChannelResponse> setReturnChannelsTTL(List<Channel> channels) {
        List<ChannelResponse> responses = new ArrayList<>();
        for (Channel channel : channels) {
            channel.setTimeToLive(channelRepository.findChannelTTL(channel.getId()));
            ChannelResponse response = new ChannelResponse(channel.getId(), channel.getChannelName(), channel.getLimitParticipants(), channel.getCurrentParticipants(), channel.getTimeToLive(), channel.getChannelHashTags(), channel.getChannelType());
            responses.add(response);
        }
        return responses;
    }
}
