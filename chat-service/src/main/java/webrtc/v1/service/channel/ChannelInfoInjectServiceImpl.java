package webrtc.v1.service.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import webrtc.v1.domain.Channel;
import webrtc.v1.exception.ChannelException.NotExistChannelException;
import webrtc.v1.repository.channel.ChannelRedisRepository;


@Service
@RequiredArgsConstructor
public class ChannelInfoInjectServiceImpl implements ChannelInfoInjectService{

    private final ChannelRedisRepository channelRedisRepository;

    /**
     * redis 저장소에 접근해 채널의 남은 수명 반환
     */
    public Channel setChannelTTL(Channel channel) {
        channel.setTimeToLive(findChannelTTL(channel.getId()));
        return channel;
    }

    public long findChannelTTL(String id) {
        long ttl = channelRedisRepository.findChannelTTL(id);
        if(isRedisTTLExpire(ttl)) {
            throw new NotExistChannelException();
        }
        return ttl;
    }

    boolean isRedisTTLExpire(long ttl) {
        return ttl == -2;
    }
}
