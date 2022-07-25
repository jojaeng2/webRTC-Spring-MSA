package webrtc.chatservice.service.chat;

import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.ChatLog;
import webrtc.chatservice.enums.ClientMessageType;

import java.util.List;

public interface ChatService {

    Long saveChatLog(ClientMessageType type, String chatMessage, String nickname, Channel channel, String senderEmail);

    void sendChatMessage(ClientMessageType type, String channelId, String nickname, String chatMessage, String senderEmail);

    List<ChatLog> findChatLogsByIndex(String channelId, Long idx);

    ChatLog findLastChatLogsByChannelId(String channelId);
}
