package webrtc.v1.service.chat;

import webrtc.v1.domain.Channel;
import webrtc.v1.domain.ChatLog;
import webrtc.v1.domain.Users;
import webrtc.v1.enums.ClientMessageType;

import java.util.List;

public interface ChatLogService {

    long saveChatLog(ClientMessageType type, String chatMessage, Channel channel, Users user);

    List<ChatLog> findChatLogsByIndex(String channelId, Long idx);

    ChatLog findLastChatLogsByChannelId(String channelId);
}
