package webrtc.chatservice.service.chat;

import webrtc.chatservice.domain.Users;
import webrtc.chatservice.dto.chat.ChattingMessage;
import webrtc.chatservice.enums.ClientMessageType;

import java.util.List;

public interface ChattingMessageFactory {

    ChattingMessage createMessage(String channelId, ClientMessageType type, String nickname, String chatMessage, Long currentParticipants, List<Users> users, Long logId, String senderEmail);

}
