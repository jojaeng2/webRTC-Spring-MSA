package webrtc.v1.chat.service;

import webrtc.v1.chat.enums.ClientMessageType;

public interface ChattingService {

    void send(ClientMessageType type, String channelId, String chatMessage, String email);

    void closeChannel(ClientMessageType type, String channelId);
}
