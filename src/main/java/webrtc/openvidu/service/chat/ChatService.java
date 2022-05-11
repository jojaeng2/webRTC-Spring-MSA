package webrtc.openvidu.service.chat;

import webrtc.openvidu.enums.ClientMessageType;

public interface ChatService {

    void sendChatMessage(ClientMessageType type, String channelId, String senderName, String chatMessage);
}
