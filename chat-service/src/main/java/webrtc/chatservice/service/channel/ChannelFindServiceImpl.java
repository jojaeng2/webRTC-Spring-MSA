package webrtc.chatservice.service.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.HashTag;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.dto.ChannelDto;
import webrtc.chatservice.dto.ChannelDto.ChannelResponse;
import webrtc.chatservice.repository.channel.ChannelDBRepository;
import webrtc.chatservice.repository.channel.ChannelRedisRepository;
import webrtc.chatservice.repository.hashtag.HashTagRepository;
import webrtc.chatservice.repository.users.UsersRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChannelFindServiceImpl implements ChannelFindService {

    private final ChannelDBRepository channelDBRepository;
    private final ChannelRedisRepository channelRedisRepository;
    private final UsersRepository usersRepository;
    private final HashTagRepository hashTagRepository;

    /*
     * 비즈니스 로직 - 모든 채널 불러오기
     *
     */
    @Transactional(readOnly = true)
    public List<ChannelResponse> findAnyChannel(String orderType, int idx) {
        switch (orderType) {
            case "partiASC" :
                return setReturnChannelsTTL(channelDBRepository.findAnyChannels(idx, "asc"));
            case "partiDESC" :
                return setReturnChannelsTTL(channelDBRepository.findAnyChannels(idx, "desc"));
        }
        return new ArrayList<>();
    }

    /*
     * 비즈니스 로직 - 입장한 모든 채널 불러오기
     *
     */
    @Transactional(readOnly = true)
    public List<ChannelResponse> findMyChannel(String orderType, String email, int idx) {
        Users user = usersRepository.findUserByEmail(email);
        switch (orderType) {
            case "partiASC" :
                return setReturnChannelsTTL(channelDBRepository.findMyChannels(user.getId(), idx, "asc"));
            case "partiDESC" :
                return setReturnChannelsTTL(channelDBRepository.findMyChannels(user.getId(), idx, "desc"));
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
    public List<ChannelResponse> findChannelByHashName(String tagName, String orderType, int idx) {
        HashTag hashTag = hashTagRepository.findHashTagByName(tagName);
        switch (orderType) {
            case "partiASC" :
                return setReturnChannelsTTL(channelDBRepository.findChannelsByHashName(hashTag, idx, "asc"));
            case "partiDESC" :
                return setReturnChannelsTTL(channelDBRepository.findChannelsByHashName(hashTag, idx, "desc"));
        }
        return new ArrayList<>();
    }

    public List<ChannelResponse> setReturnChannelsTTL(List<Channel> channels) {

        return channels.stream()
                .map(channel -> {
                    Long ttl = channelRedisRepository.findChannelTTL(channel.getId());
                    channel.setTimeToLive(ttl);
                    return new ChannelResponse(channel.getId(), channel.getChannelName(), channel.getLimitParticipants(), channel.getCurrentParticipants(), channel.getTimeToLive(), channel.getChannelHashTags(), channel.getChannelType());
                })
                .collect(Collectors.toList());
    }
}
