package webrtc.chatservice.service.chat;

import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.ChatLog;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.enums.ClientMessageType;

import java.util.List;

public interface ChatLogService {

    long saveChatLog(ClientMessageType type, String chatMessage, Channel channel, Users user);

    List<ChatLog> findChatLogsByIndex(String channelId, Long idx);

    ChatLog findLastChatLogsByChannelId(String channelId);
}
