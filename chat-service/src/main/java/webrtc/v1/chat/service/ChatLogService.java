package webrtc.v1.chat.service;

import java.util.List;
import webrtc.v1.channel.entity.Channel;
import webrtc.v1.chat.entity.ChatLog;
import webrtc.v1.chat.enums.ClientMessageType;
import webrtc.v1.user.entity.Users;

public interface ChatLogService {

  long save(ClientMessageType type, String chatMessage, Channel channel, Users user);

  List<ChatLog> findChatLogsByIndex(String channelId, int idx);

  int findLastIndexByChannelId(String id);
}
