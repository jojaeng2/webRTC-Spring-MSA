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
    private final UsersRepository usersRepository;
    private final HashTagRepository hashTagRepository;
    private final ChannelInfoInjectService channelInfoInjectService;

    /*
     * 비즈니스 로직 - 모든 채널 불러오기
     *
     */
    @Transactional(readOnly = true)
    public List<ChannelResponse> findAnyChannel(String orderType, int idx) {
        switch (orderType) {
            case "partiASC" :
                return channelInfoInjectService.setReturnChannelsTTL(channelDBRepository.findAnyChannels(idx, "asc"));
            case "partiDESC" :
                return channelInfoInjectService.setReturnChannelsTTL(channelDBRepository.findAnyChannels(idx, "desc"));
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
                return channelInfoInjectService.setReturnChannelsTTL(channelDBRepository.findMyChannels(user.getId(), idx, "asc"));
            case "partiDESC" :
                return channelInfoInjectService.setReturnChannelsTTL(channelDBRepository.findMyChannels(user.getId(), idx, "desc"));
        }
        return new ArrayList<>();
    }

    /*
     * 비즈니스 로직 - 특정 채널 ID로 찾기
     */
    @Transactional(readOnly = true)
    public Channel findOneChannelById(String channelId) {
        Channel channel = channelDBRepository.findChannelById(channelId);
        return channelInfoInjectService.setChannelTTL(channel);
    }

    @Transactional(readOnly = true)
    public List<ChannelResponse> findChannelByHashName(String tagName, String orderType, int idx) {
        HashTag hashTag = hashTagRepository.findHashTagByName(tagName);
        switch (orderType) {
            case "partiASC" :
                return channelInfoInjectService.setReturnChannelsTTL(channelDBRepository.findChannelsByHashName(hashTag, idx, "asc"));
            case "partiDESC" :
                return channelInfoInjectService.setReturnChannelsTTL(channelDBRepository.findChannelsByHashName(hashTag, idx, "desc"));
        }
        return new ArrayList<>();
    }
}
