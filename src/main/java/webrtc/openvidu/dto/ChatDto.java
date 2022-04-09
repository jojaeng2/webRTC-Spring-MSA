package webrtc.openvidu.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import webrtc.openvidu.domain.User;
import webrtc.openvidu.enums.ClientMessageType;
import webrtc.openvidu.enums.SocketServerMessageType;

import java.util.HashMap;
import java.util.Map;

public class ChatDto {

    @Getter
    public static class ChatServerMessage extends PublishMessage {

        private String userName;
        private String chatMessage;

        public ChatServerMessage(SocketServerMessageType type, String userName, String chatMessage) {
            super(type);
            this.userName = userName;
            this.chatMessage = chatMessage;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ClientMessage {
        private ClientMessageType type;
        private String channelId;
        private String senderName;
        private String message;

        public ClientMessage(ClientMessageType type, String channelId, String senderName) {
            this.type = type;
            this.channelId = channelId;
            this.senderName = senderName;
            this.message = "";
        }
    }

    @Getter
    public static class PublishMessage {
        private SocketServerMessageType type;

        public PublishMessage(SocketServerMessageType type) {
            this.type = type;
        }
    }

    @Getter
    public static class ServerMessage extends PublishMessage {
        private String channelId;
        private String senderName;
        private String message;
        private Long userCount;
        private Map<Long, User> users = new HashMap<>();

        public void setSenderName(String senderName) {
            this.senderName = senderName;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public ServerMessage(SocketServerMessageType type, String message, String channelId, Long userCount) {
            super(type);
            this.channelId = channelId;
            this.message = message;
            this.userCount = userCount;
        }
        public ServerMessage(SocketServerMessageType type, String channelId, String senderName, String message, Long userCount) {
            super(type);
            this.channelId = channelId;
            this.senderName = senderName;
            this.message = message;
            this.userCount = userCount;
        }
    }

}
