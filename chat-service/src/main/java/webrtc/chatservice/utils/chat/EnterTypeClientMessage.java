package webrtc.chatservice.utils.chat;

import webrtc.chatservice.dto.chat.ClientMessage;
import webrtc.chatservice.enums.ClientMessageType;

public class EnterTypeClientMessage implements CreateClientMessage{

    @Override
    public void build(ClientMessageType type, ClientMessage overallMessage, String nickname, String userId, String channelId) {
        overallMessage.setMessage("[알림] " + nickname+ " 님이 채팅방에 입장했습니다.");
    }
}
