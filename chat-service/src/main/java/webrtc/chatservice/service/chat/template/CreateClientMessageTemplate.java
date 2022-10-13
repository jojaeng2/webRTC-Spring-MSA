package webrtc.chatservice.service.chat.template;

import webrtc.chatservice.dto.chat.ClientMessage;
import webrtc.chatservice.enums.ClientMessageType;

@FunctionalInterface
public interface CreateClientMessageTemplate {

    void build (ClientMessageType type, ClientMessage overallMessage, String nickname, String userId, String channelId);
}
