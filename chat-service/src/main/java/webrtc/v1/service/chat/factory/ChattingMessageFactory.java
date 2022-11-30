package webrtc.v1.service.chat.factory;

import webrtc.v1.domain.Channel;
import webrtc.v1.domain.Users;
import webrtc.v1.dto.chat.ChattingMessage;
import webrtc.v1.enums.ClientMessageType;

import java.util.List;

public interface ChattingMessageFactory {

    ChattingMessage createMessage(Channel channel, ClientMessageType type, String chatMessage, List<Users> users, long logId, Users user);

}
