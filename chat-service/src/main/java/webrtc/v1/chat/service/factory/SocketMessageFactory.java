package webrtc.v1.chat.service.factory;

import webrtc.v1.user.entity.Users;
import webrtc.v1.chat.dto.ClientMessage;
import webrtc.v1.chat.enums.ClientMessageType;

public interface SocketMessageFactory {

    void execute(ClientMessageType type, ClientMessage overallMessage, Users user, String channelId);

}
