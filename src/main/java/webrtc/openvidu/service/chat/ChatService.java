package webrtc.openvidu.service.chat;

import webrtc.openvidu.domain.Channel;
import webrtc.openvidu.domain.ChatLog;
import webrtc.openvidu.enums.ChatEnumType;
import webrtc.openvidu.enums.ClientMessageType;

import java.util.List;

public interface ChatService {

    void saveChatMessage(ChatEnumType type, String chatMessage, String username, Channel channel);

    void sendChatMessage(ClientMessageType type, String channelId, String senderName, String chatMessage);

    List<ChatLog> findChatLogsByIndex(String channelId, int idx);
}
