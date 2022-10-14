package webrtc.chatservice.service.chat.template;

import webrtc.chatservice.domain.Users;
import webrtc.chatservice.dto.chat.ClientMessage;
import webrtc.chatservice.enums.ClientMessageType;

@FunctionalInterface
public interface CreateClientMessageTemplate {

    void build (ClientMessage overallMessage, Users user, String channelId);
}
