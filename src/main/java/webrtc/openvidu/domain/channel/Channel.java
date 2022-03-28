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
    private String channelName;
    private Long limitParticipants;
    private Long currentParticipants;
    private Long timeToLive;
    private Map<Long, User> users;

    public Channel(String channelName, Long limitParticipants) {
        this.id = UUID.randomUUID().toString();
        this.channelName = channelName;
        this.limitParticipants = limitParticipants;
        this.currentParticipants = 1L;
        this.timeToLive = 24*60*60L;
        this.users = new HashMap<>();
    }

    public void addUser(User user) {
        this.users.put(user.getUserId(), user);
    }

    public void delUser(User user) {
        this.users.remove(user.getUserId());
    }

    public void setTimeToLive(Long timeToLive) {
        this.timeToLive = timeToLive;
    }

}
