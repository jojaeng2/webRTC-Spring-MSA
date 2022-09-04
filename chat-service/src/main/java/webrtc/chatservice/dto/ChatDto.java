package webrtc.chatservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import webrtc.chatservice.domain.ChatLog;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.enums.SocketServerMessageType;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static webrtc.chatservice.enums.SocketServerMessageType.*;

public class ChatDto {


    @Getter
    @NoArgsConstructor
    public static class ClientMessage {
        private String senderName;
        private String message;

        public void setSenderName(String senderName) {
            this.senderName = senderName;
        }
    }

    public interface CreateMessage {
        ChattingMessage setFields(String channelId, String nickname, String chatMessage, Long currentParticipants, List<Users> users, Long logId, String senderEmail);
    }

    @NoArgsConstructor
    @Getter
    public static class ChattingMessage {
        private String channelId;
        private SocketServerMessageType type;
        private String nickname;
        private String chatMessage;
        private Long currentParticipants;
        private List<Users> users;
        private Long logId;
        private String senderEmail;
        private Timestamp sendTime;

        public ChattingMessage(String channelId, SocketServerMessageType type, String nickname, String chatMessage, Long currentParticipants, List<Users> users, Long logId, String senderEmail) {
            this.channelId = channelId;
            this.type = type;
            this.nickname = nickname;
            this.chatMessage = chatMessage;
            this.currentParticipants = currentParticipants;
            this.users = users;
            this.logId = logId;
            this.senderEmail = senderEmail;
            this.sendTime = new Timestamp(System.currentTimeMillis());
        }

    }

    @Getter
    @NoArgsConstructor
    public static class ChatTypeMessage implements CreateMessage{
        public ChattingMessage setFields(String channelId, String nickname, String chatMessage, Long currentParticipants, List<Users> users, Long logId, String senderEmail) {
            return new ChattingMessage(channelId, CHAT, nickname, chatMessage, currentParticipants, users, logId, senderEmail);
        }
    }

    @NoArgsConstructor
    @Getter
    public static class EnterTypeMessage implements CreateMessage {
        public ChattingMessage setFields(String channelId, String nickname, String chatMessage, Long currentParticipants, List<Users> users, Long logId, String senderEmail) {
            return new ChattingMessage(channelId, RENEWAL, nickname, chatMessage, currentParticipants, users, logId, senderEmail);
        }
    }

    @NoArgsConstructor
    @Getter
    public static class ExitTypeMessage implements CreateMessage {
        public ChattingMessage setFields(String channelId, String nickname, String chatMessage, Long currentParticipants, List<Users> users, Long logId, String senderEmail) {
            return new ChattingMessage(channelId, RENEWAL, nickname, chatMessage, currentParticipants, users, logId, senderEmail);
        }
    }

    @NoArgsConstructor
    @Getter
    public static class CloseTypeMessage implements CreateMessage {
        public ChattingMessage setFields(String channelId, String nickname, String chatMessage, Long currentParticipants, List<Users> users, Long logId, String senderEmail) {
            return new ChattingMessage(channelId, CLOSE, nickname, chatMessage, currentParticipants, users, logId, senderEmail);
        }
    }

    @NoArgsConstructor
    @Getter
    public static class ReenterTypeMessage implements CreateMessage {
        public ChattingMessage setFields(String channelId, String nickname, String chatMessage, Long currentParticipants, List<Users> users, Long logId, String senderEmail) {
            return new ChattingMessage(channelId, RENEWAL, nickname, chatMessage, currentParticipants, users, logId, senderEmail);
        }
    }

    @NoArgsConstructor
    @Getter
    public static class CreateTypeMessage implements CreateMessage {
        public ChattingMessage setFields(String channelId, String nickname, String chatMessage, Long currentParticipants, List<Users> users, Long logId, String senderEmail) {
            return new ChattingMessage(channelId, CREATE, nickname, chatMessage, currentParticipants, users, logId, senderEmail);
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FindChatLogsResponse {
        private List<ChatLog> logs;
    }
}
