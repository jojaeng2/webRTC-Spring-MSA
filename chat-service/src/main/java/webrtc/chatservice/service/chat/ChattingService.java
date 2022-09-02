package webrtc.chatservice.service.chat;

import webrtc.chatservice.enums.ClientMessageType;

public interface ChattingService {

    void sendChatMessage(ClientMessageType type, String channelId, String nickname, String chatMessage, String senderEmail);

}
