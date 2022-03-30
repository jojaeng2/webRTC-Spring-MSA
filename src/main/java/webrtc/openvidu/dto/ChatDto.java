package webrtc.openvidu.dto;

import lombok.Getter;
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
    public static class ClientMessage {
        private ClientMessageType type;
        private String channelId;
        private Long userId;
        private Long userName;
        private String message;
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
        private String message;
        private Map<Long, User> users = new HashMap<>();

        public ServerMessage(SocketServerMessageType type, String channelId, String message, Map<Long, User> users) {
            super(type);
            this.channelId = channelId;
            this.message = message;
            this.users = users;
        }
    }

}
