package webrtc.openvidu.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import webrtc.openvidu.domain.User;
import webrtc.openvidu.enums.ClientMessageType;
import webrtc.openvidu.enums.SocketServerMessageType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatDto {

<<<<<<< HEAD:backend/src/main/java/webrtc/openvidu/dto/ChatDto.java
=======

>>>>>>> 18cdb6bb39d5ef24329da9b5e0f6d53d239ef993:src/main/java/webrtc/openvidu/dto/ChatDto.java
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

<<<<<<< HEAD:backend/src/main/java/webrtc/openvidu/dto/ChatDto.java
        public ChatServerMessage(SocketServerMessageType type, String channelId, String senderName, String chatMessage) {
            super(type, channelId);
            this.senderName = senderName;
            this.chatMessage = chatMessage;
=======
        public ChatServerMessage(String channelId) {
            super(channelId);
>>>>>>> 18cdb6bb39d5ef24329da9b5e0f6d53d239ef993:src/main/java/webrtc/openvidu/dto/ChatDto.java
        }

<<<<<<< HEAD:backend/src/main/java/webrtc/openvidu/dto/ChatDto.java
    @Getter
    @NoArgsConstructor
    public static class ServerNoticeMessage extends PublishMessage {
        private String senderName;
        private String chatMessage;
        private Long userCount;
        private Map<Long, User> users = new HashMap<>();

        public ServerNoticeMessage(SocketServerMessageType type, String channelId, String senderName, String chatMessage, Long userCount) {
            super(type, channelId);
            this.senderName = senderName;
            this.chatMessage = chatMessage;
            this.userCount = userCount;
=======
        public void setChatType(SocketServerMessageType type, String senderName, String chatMessage, Long currentParticipants, List<User> users) {
            this.setType(type);
            this.senderName = senderName;
            this.chatMessage = chatMessage;
            this.currentParticipants = currentParticipants;
            this.users = users;
        }

        public void setEnterType(SocketServerMessageType type, String senderName, String chatMessage, Long currentParticipants, List<User> users) {
            this.setType(type);
            this.senderName = senderName;
            this.chatMessage = chatMessage;
            this.currentParticipants = currentParticipants;
            this.users = users;
        }

        public void setExitType(SocketServerMessageType type, String senderName, String chatMessage, Long currentParticipants, List<User> users) {
            this.setType(type);
            this.senderName = senderName;
            this.chatMessage = chatMessage;
            this.currentParticipants = currentParticipants;
            this.users = users;
>>>>>>> 18cdb6bb39d5ef24329da9b5e0f6d53d239ef993:src/main/java/webrtc/openvidu/dto/ChatDto.java
        }
    }
}
