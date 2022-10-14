package webrtc.chatservice.service.chat;

import webrtc.chatservice.domain.Users;
import webrtc.chatservice.enums.ClientMessageType;

public interface ChattingService {

    void sendChatMessage(ClientMessageType type, String channelId, String chatMessage, Users user);

}
