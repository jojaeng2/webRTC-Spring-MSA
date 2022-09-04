package webrtc.chatservice.service.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.controller.HttpApiController;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.HashTag;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.dto.ChannelDto;
import webrtc.chatservice.repository.channel.ChannelDBRepository;
import webrtc.chatservice.repository.channel.ChannelRedisRepository;
import webrtc.chatservice.repository.channel.ChannelUserRepository;
import webrtc.chatservice.repository.hashtag.HashTagRepository;
import webrtc.chatservice.repository.users.UsersRepository;
import webrtc.chatservice.service.rabbit.RabbitPublish;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChannelFindServiceImpl implements ChannelFindService {

    private final ChannelDBRepository channelDBRepository;
    private final ChannelRedisRepository channelRedisRepository;
    private final UsersRepository usersRepository;
    private final ChannelUserRepository channelUserRepository;
    private final HashTagRepository hashTagRepository;
    private final RabbitPublish rabbitPublish;

    // 30분당 100포인트
    private final Long pointUnit = 100L;
    private final Long channelCreatePoint = 2L;
    private final Long channelExtensionMinute = 30L;
    private final HttpApiController httpApiController;

    /*
     * 비즈니스 로직 - 모든 채널 불러오기
     *
     */
    @Transactional(readOnly = true)
    public List<ChannelDto.ChannelResponse> findAnyChannel(String orderType, int idx) {
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
    @Transactional(readOnly = true)
    public List<ChannelDto.ChannelResponse> findMyChannel(String orderType, String email, int idx) {
        Users user = usersRepository.findUserByEmail(email);
        switch (orderType) {
            case "partiASC" :
                return setReturnChannelsTTL(channelDBRepository.findMyChannelByPartiASC(user.getId(), idx));
            case "partiDESC" :
                return setReturnChannelsTTL(channelDBRepository.findMyChannelByPartiDESC(user.getId(), idx));
        }
        return new ArrayList<>();
    }

    /*
     * 비즈니스 로직 - 특정 채널 ID로 찾기
     */
    @Transactional(readOnly = true)
    public Channel findOneChannelById(String channelId) {
        Channel channel = channelDBRepository.findChannelById(channelId);
        Long ttl = channelRedisRepository.findChannelTTL(channelId);
        channel.setTimeToLive(ttl);
        return channel;
    }

    @Transactional(readOnly = true)
    public List<ChannelDto.ChannelResponse> findChannelByHashName(String tagName, String orderType, int idx) {
        HashTag hashTag = hashTagRepository.findHashTagByName(tagName);
        switch (orderType) {
            case "partiASC" :
                return setReturnChannelsTTL(channelDBRepository.findChannelsByHashNameAndPartiASC(hashTag, idx));
            case "partiDESC" :
                return setReturnChannelsTTL(channelDBRepository.findChannelsByHashNameAndPartiDESC(hashTag, idx));
        }
        return new ArrayList<>();
    }

    public List<ChannelDto.ChannelResponse> setReturnChannelsTTL(List<Channel> channels) {

        return channels.stream()
                .map(channel -> {
                    Long ttl = channelRedisRepository.findChannelTTL(channel.getId());
                    channel.setTimeToLive(ttl);
                    return new ChannelDto.ChannelResponse(channel.getId(), channel.getChannelName(), channel.getLimitParticipants(), channel.getCurrentParticipants(), channel.getTimeToLive(), channel.getChannelHashTags(), channel.getChannelType());
                })
                .collect(Collectors.toList());
    }
}
