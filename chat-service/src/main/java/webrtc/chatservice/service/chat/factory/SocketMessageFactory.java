package webrtc.chatservice.service.chat.factory;

import webrtc.chatservice.dto.chat.ClientMessage;
import webrtc.chatservice.enums.ClientMessageType;

public interface SocketMessageFactory {

    void execute(ClientMessageType type, ClientMessage overallMessage, String nickname, String userId, String channelId);

}
