package webrtc.v1.chat.service;

import webrtc.v1.channel.entity.Channel;
import webrtc.v1.chat.enums.ClientMessageType;

import java.util.UUID;

public interface ChattingService {

    void send(ClientMessageType type, String channelId, String chatMessage, String userId);

    void closeChannel(Channel channel);
}
