package webrtc.chatservice.service.chat.template;

import webrtc.chatservice.dto.chat.ClientMessage;
import webrtc.chatservice.enums.ClientMessageType;

public class ChatTypeClientMessageTemplate implements CreateClientMessageTemplate {


    @Override
    public void build(ClientMessageType type, ClientMessage overallMessage, String nickname, String userId, String channelId) {
        overallMessage.setSenderName(nickname);
    }
}
