package webrtc.chatservice.service.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.HashTag;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.dto.ChannelDto.ChannelResponse;
import webrtc.chatservice.exception.ChannelException.NotExistChannelException;
import webrtc.chatservice.exception.HashTagException.NotExistHashTagException;
import webrtc.chatservice.exception.UserException.NotExistUserException;
import webrtc.chatservice.repository.channel.ChannelCrudRepository;
import webrtc.chatservice.repository.channel.ChannelListRepository;
import webrtc.chatservice.repository.hashtag.HashTagRepository;
import webrtc.chatservice.repository.users.UsersRepository;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ChannelFindServiceImpl implements ChannelFindService {

    private final ChannelListRepository channelListRepository;
    private final ChannelCrudRepository channelCrudRepository;
    private final UsersRepository usersRepository;
    private final HashTagRepository hashTagRepository;
    private final ChannelInfoInjectService channelInfoInjectService;
    private final Map<String, String> orderMap = new HashMap<>();

    /**
     * 채널 정렬 기준을 저장하고 있는 HashMap 생성 및 관리
     * Default 값 = 'asc'
     */
    @PostConstruct
    private void createMapping() {
        orderMap.put("partiASC", "asc");
        orderMap.put("partiDESC", "desc");
    }

    /*
     * 비즈니스 로직 - 특정 채널을 ID로 찾고, 남은 수명을 넣어 반환
     */
    @Transactional(readOnly = true)
    public Channel findOneChannelById(String channelId) {
        Channel channel = channelCrudRepository.findById(channelId)
                .orElseThrow(NotExistChannelException::new);
        return channelInfoInjectService.setChannelTTL(channel);
    }

    /*
     * 비즈니스 로직 - 조건 없이 모든 채널을 목록으로 불러옴
     */
    @Transactional(readOnly = true)
    public List<Channel> findAnyChannel(String orderType, int idx) {
        return channelListRepository.findAnyChannels(idx, findOrderType(orderType))
                .stream()
                .map(channelInfoInjectService::setChannelTTL)
                .collect(toList());
    }

    /*
     * 비즈니스 로직 - 특정 회원이 입장한 채널만 목록으로 불러옴
     */
    @Transactional(readOnly = true)
    public List<Channel> findMyChannel(String orderType, String email, int idx) {
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(NotExistUserException::new);

        return channelListRepository.findMyChannels(user.getId(), idx, findOrderType(orderType))
                .stream()
                .map(channelInfoInjectService::setChannelTTL)
                .collect(toList());
    }

    /*
     * 비즈니스 로직 - 특정 해시 태그를 가지고 있는 채널만 목록으로 불러옴
     */
    @Transactional(readOnly = true)
    public List<Channel> findChannelByHashName(String tagName, String orderType, int idx) {
        HashTag hashTag = hashTagRepository.findByTagName(tagName)
                .orElseThrow(NotExistHashTagException::new);

        return channelListRepository.findChannelsByHashName(hashTag, idx, findOrderType(orderType))
                .stream()
                .map(channelInfoInjectService::setChannelTTL)
                .collect(toList());
    }

    /*
     * 비즈니스 로직 - 최근 보내진 채팅 메시지 시간에 따라 정렬하여 채널 목록으로 불러옴
     */
    @Transactional(readOnly = true)
    public List<Channel> findChannelsRecentlyTalk(String orderType, int idx) {

        return channelListRepository.findChannelsRecentlyTalk(idx, findOrderType(orderType))
                .stream()
                .map(channelInfoInjectService::setChannelTTL)
                .collect(toList());
    }

    /*
     * 비즈니스 로직 - HashMap에 들어있는 정렬 기준을 반환
     * Default = asc
     */
    private String findOrderType(String type) {
        if(orderMap.containsKey(type)) return orderMap.get(type);
        return "asc";
    }
}
