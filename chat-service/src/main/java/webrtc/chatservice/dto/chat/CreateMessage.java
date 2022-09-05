package webrtc.chatservice.dto.chat;

import webrtc.chatservice.domain.Users;

import java.util.List;

public interface CreateMessage {
    ChattingMessage setFields(String channelId, String nickname, String chatMessage, Long currentParticipants, List<Users> users, Long logId, String senderEmail);

}
