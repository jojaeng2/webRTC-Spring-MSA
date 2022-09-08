package webrtc.chatservice.service.rabbit;

import webrtc.chatservice.dto.chat.ChattingMessage;
import webrtc.chatservice.enums.ClientMessageType;

public interface RabbitMessageFactory {
    void execute(ChattingMessage serverMessage, ClientMessageType type);
}
