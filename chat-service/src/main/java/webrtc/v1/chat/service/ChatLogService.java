package webrtc.v1.chat.service;

import webrtc.v1.channel.entity.Channel;
import webrtc.v1.chat.entity.ChatLog;
import webrtc.v1.user.entity.Users;
import webrtc.v1.enums.ClientMessageType;

import java.util.List;

public interface ChatLogService {

    long save(ClientMessageType type, String chatMessage, Channel channel, Users user);

    List<ChatLog> findChatLogsByIndex(String channelId, Long idx);

    Long findLastIndexByChannelId(String id);
}
