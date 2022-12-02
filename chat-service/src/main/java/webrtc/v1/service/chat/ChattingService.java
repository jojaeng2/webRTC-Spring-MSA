package webrtc.v1.service.chat;

import webrtc.v1.domain.Users;
import webrtc.v1.enums.ClientMessageType;

public interface ChattingService {

    void sendChatMessage(ClientMessageType type, String channelId, String chatMessage, Users user);

}
