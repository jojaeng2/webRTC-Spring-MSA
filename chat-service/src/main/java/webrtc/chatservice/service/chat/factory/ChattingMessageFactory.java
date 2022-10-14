package webrtc.chatservice.service.chat.factory;

import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.dto.chat.ChattingMessage;
import webrtc.chatservice.enums.ClientMessageType;

import java.util.List;

public interface ChattingMessageFactory {

    ChattingMessage createMessage(Channel channel, ClientMessageType type, String chatMessage, List<Users> users, long logId, Users user);

}
