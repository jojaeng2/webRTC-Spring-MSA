package webrtc.chatservice.dto.chat;

import webrtc.chatservice.enums.ClientMessageType;

public interface CreateClientMessage {

    void build (ClientMessageType type, ClientMessage overallMessage, String nickname, String userId, String channelId);
}
