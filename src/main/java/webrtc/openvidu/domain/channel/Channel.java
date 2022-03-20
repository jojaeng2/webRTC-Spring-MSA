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
    private Long  currentParticipants;
    private Map<Long, User> users = new HashMap<>();

    public Channel(String channelName, Long limitParticipants, Long channelNum) {
        this.id = UUID.randomUUID().toString();
        this.channelNum = channelNum;
        this.channelName = channelName;
        this.limitParticipants = limitParticipants;
        this.currentParticipants = 1L;
    }

    public void enterUser(User user) {
        this.users.put(user.getUserId(), user);
    }
}
