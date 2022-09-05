package webrtc.chatservice.dto.chat;

import webrtc.chatservice.domain.Users;

import java.util.List;

import static webrtc.chatservice.enums.SocketServerMessageType.RENEWAL;

public class EnterTypeMessage implements CreateMessage {
    public ChattingMessage setFields(String channelId, String nickname, String chatMessage, Long currentParticipants, List<Users> users, Long logId, String senderEmail) {
        return new ChattingMessage(channelId, RENEWAL, nickname, chatMessage, currentParticipants, users, logId, senderEmail);
    }
}
