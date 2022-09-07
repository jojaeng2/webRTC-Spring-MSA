package webrtc.chatservice.dto.chat;

import webrtc.chatservice.enums.ClientMessageType;

public class ChatTypeClientMessage implements CreateClientMessage{


    @Override
    public void build(ClientMessageType type, ClientMessage overallMessage, String nickname, String userId, String channelId) {
        overallMessage.setSenderName(nickname);
    }
}
