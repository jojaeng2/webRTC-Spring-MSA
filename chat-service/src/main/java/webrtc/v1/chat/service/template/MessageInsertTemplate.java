package webrtc.v1.chat.service.template;

import webrtc.v1.user.entity.Users;
import webrtc.v1.chat.dto.ClientMessage;

@FunctionalInterface
public interface MessageInsertTemplate {

    void execute (ClientMessage overallMessage, Users user, String channelId);
}
