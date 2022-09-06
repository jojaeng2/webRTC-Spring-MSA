package webrtc.chatservice.dto.chat;

import webrtc.chatservice.domain.Users;
import webrtc.chatservice.enums.SocketServerMessageType;

import java.util.List;

public interface CreateMessage {

    ChattingMessage setMessageType(ChattingMessage type);

}
