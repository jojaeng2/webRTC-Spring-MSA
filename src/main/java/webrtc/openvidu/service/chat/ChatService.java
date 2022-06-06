package webrtc.openvidu.service.chat;

import webrtc.openvidu.domain.Channel;
import webrtc.openvidu.domain.ChatLog;
import webrtc.openvidu.enums.ClientMessageType;

import java.util.List;

public interface ChatService {

    Long saveChatLog(ClientMessageType type, String chatMessage, String username, Channel channel, String senderEmail);

    void sendChatMessage(ClientMessageType type, String channelId, String senderName, String chatMessage, String senderEmail);

    List<ChatLog> findChatLogsByIndex(String channelId, Long idx);

    ChatLog findLastChatLogsByChannelId(String channelId);
}
