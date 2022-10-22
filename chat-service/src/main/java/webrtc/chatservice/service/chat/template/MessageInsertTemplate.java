package webrtc.chatservice.service.chat.template;

import webrtc.chatservice.domain.Users;
import webrtc.chatservice.dto.chat.ClientMessage;

@FunctionalInterface
public interface MessageInsertTemplate {

    void execute (ClientMessage overallMessage, Users user, String channelId);
}
