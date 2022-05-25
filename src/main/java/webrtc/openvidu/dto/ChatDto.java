package webrtc.openvidu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import webrtc.openvidu.domain.ChatLog;
import webrtc.openvidu.domain.User;
import webrtc.openvidu.enums.ClientMessageType;
import webrtc.openvidu.enums.SocketServerMessageType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatDto {


    @Getter
    @NoArgsConstructor
    public static class ClientMessage {
        private ClientMessageType type;
        private String channelId;
        private String senderName;
        private String message;


        public void setSenderName(String senderName) {
            this.senderName = senderName;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class PublishMessage {
        private SocketServerMessageType type;
        private String channelId;

        public PublishMessage(SocketServerMessageType type) {
            this.type = type;
        }

        public PublishMessage(String channelId) {
            this.channelId = channelId;
        }

        public PublishMessage(SocketServerMessageType type, String channelId) {
            this.type = type;
            this.channelId = channelId;
        }

        public void setType(SocketServerMessageType type) {
            this.type = type;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ChatServerMessage extends PublishMessage {

        private String senderName;
        private String chatMessage;
        private Long currentParticipants;
        private List<User> users = new ArrayList<>();
        private Long logId;

        public ChatServerMessage(String channelId) {
            super(channelId);
        }

        public void setMessageType(SocketServerMessageType type, String senderName, String chatMessage, Long currentParticipants, List<User> users) {
            this.setType(type);
            this.senderName = senderName;
            this.chatMessage = chatMessage;
            this.currentParticipants = currentParticipants;
            this.users = users;
        }

        public void setChatLogId(Long logId) {
            this.logId = logId;
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FindChatLogsResponse {
        private List<ChatLog> logs;
    }
}
