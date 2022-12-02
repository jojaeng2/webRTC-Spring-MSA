package webrtc.v1.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class ClientMessage {

    private String senderName;
    private String message;


}
