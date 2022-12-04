package webrtc.v1.chat.service.factory;

import webrtc.v1.channel.entity.Channel;
import webrtc.v1.user.entity.Users;
import webrtc.v1.chat.dto.ChattingMessage;
import webrtc.v1.chat.enums.ClientMessageType;

import java.util.List;

public interface ChattingMessageFactory {

    ChattingMessage createMessage(Channel channel, ClientMessageType type, String chatMessage, List<Users> users, long logId, Users user);

    ChattingMessage closeMessage(Channel channel);
}
