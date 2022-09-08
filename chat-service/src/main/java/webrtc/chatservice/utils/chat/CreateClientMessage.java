package webrtc.chatservice.utils.chat;

import webrtc.chatservice.dto.chat.ClientMessage;
import webrtc.chatservice.enums.ClientMessageType;

public interface CreateClientMessage {

    void build (ClientMessageType type, ClientMessage overallMessage, String nickname, String userId, String channelId);
}
