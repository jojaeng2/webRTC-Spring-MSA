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
    }

    @Getter
    public static class ChatServerMessage extends PublishMessage {

        private String senderName;
        private String chatMessage;

        public ChatServerMessage(SocketServerMessageType type, String channelId, String senderName, String chatMessage) {
            super(type, channelId);
            this.senderName = senderName;
            this.chatMessage = chatMessage;
        }
    }

    @Getter
    public static class ServerNoticeMessage extends PublishMessage {
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

        public ServerNoticeMessage(SocketServerMessageType type, String channelId, String message, Long userCount) {
            super(type, channelId);
            this.message = message;
            this.userCount = userCount;
        }
        public ServerNoticeMessage(SocketServerMessageType type, String channelId, String senderName, String message, Long userCount) {
            super(type, channelId);
            this.senderName = senderName;
            this.message = message;
            this.userCount = userCount;
        }
    }

}
