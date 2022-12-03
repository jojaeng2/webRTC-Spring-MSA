package webrtc.v1.chat.service;

import webrtc.v1.user.entity.Users;
import webrtc.v1.enums.ClientMessageType;

public interface ChattingService {

    void sendChatMessage(ClientMessageType type, String channelId, String chatMessage, String email);

    void closeChannel(ClientMessageType type, String channelId);
}
