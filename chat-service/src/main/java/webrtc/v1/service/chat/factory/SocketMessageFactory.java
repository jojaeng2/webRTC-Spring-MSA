package webrtc.v1.service.chat.factory;

import webrtc.v1.domain.Users;
import webrtc.v1.dto.chat.ClientMessage;
import webrtc.v1.enums.ClientMessageType;

public interface SocketMessageFactory {

    void execute(ClientMessageType type, ClientMessage overallMessage, Users user, String channelId);

}
