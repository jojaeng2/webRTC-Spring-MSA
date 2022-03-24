package webrtc.openvidu.dto.chat;

import lombok.Getter;
import webrtc.openvidu.enums.SocketServerMessageType;

@Getter
public class ChatServerMessage extends PublishMessage{

    private String userName;
    private String chatMessage;

    public ChatServerMessage(SocketServerMessageType type, String userName, String chatMessage) {
        super(type);
        this.userName = userName;
        this.chatMessage = chatMessage;
    }
}
