package webrtc.v1.channel.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import webrtc.v1.channel.entity.Channel;
import webrtc.v1.channel.exception.ChannelException.NotExistChannelException;
import webrtc.v1.channel.repository.ChannelRedisRepository;

import static webrtc.v1.channel.enums.ChannelInfo.EXPIRE;


@Service
@RequiredArgsConstructor
public class ChannelInfoInjectServiceImpl implements ChannelInfoInjectService{

    private final ChannelRedisRepository channelRedisRepository;

    /**
     * redis 저장소에 접근해 채널의 남은 수명 반환
     */
    public Channel setTtl(Channel channel) {
        channel.setTimeToLive(findTtl(channel.getId()));
        return channel;
    }

    public long findTtl(String id) {
        long ttl = channelRedisRepository.findTtl(id);
        if(isExpire(ttl)) {
            throw new NotExistChannelException();
        }
        return ttl;
    }

    private boolean isExpire(long ttl) {
        return ttl == EXPIRE.getTtl();
    }
}
