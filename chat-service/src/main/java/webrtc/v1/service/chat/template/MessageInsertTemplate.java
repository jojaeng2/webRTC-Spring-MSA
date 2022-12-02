package webrtc.v1.service.chat.template;

import webrtc.v1.domain.Users;
import webrtc.v1.dto.chat.ClientMessage;

@FunctionalInterface
public interface MessageInsertTemplate {

    void execute (ClientMessage overallMessage, Users user, String channelId);
}
