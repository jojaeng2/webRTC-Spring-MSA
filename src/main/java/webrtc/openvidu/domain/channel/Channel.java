package webrtc.openvidu.domain.channel;

import lombok.Getter;
import webrtc.openvidu.domain.User;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class Channel implements Serializable {

    private static final Long serialVersionUID = 1L;
    private String id;
    private Long channelNum;
    private String channelName;
    private Long limitParticipants;
    private Map<Long, User> users = new HashMap<>();

    public static Channel create(String channelName) {
        Channel channel = new Channel();
        channel.channelName = channelName;
        channel.id = UUID.randomUUID().toString();
        return channel;
    }
}
