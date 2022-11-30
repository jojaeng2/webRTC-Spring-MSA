package webrtc.v1.dto.chat;

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
