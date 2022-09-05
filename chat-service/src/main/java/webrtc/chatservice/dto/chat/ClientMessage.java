package webrtc.chatservice.dto.chat;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ClientMessage {

    private String senderName;
    private String message;

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}
