package webrtc.chatservice.service.chat.factory;

import webrtc.chatservice.domain.Users;
import webrtc.chatservice.dto.chat.ClientMessage;
import webrtc.chatservice.enums.ClientMessageType;

public interface SocketMessageFactory {

    void execute(ClientMessageType type, ClientMessage overallMessage, Users user, String channelId);

}
