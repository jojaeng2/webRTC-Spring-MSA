package webrtc.chatservice.service.rabbit.factory;

import webrtc.chatservice.dto.chat.ChattingMessage;
import webrtc.chatservice.enums.ClientMessageType;

public interface RabbitMessageFactory {

    void messageFactoryConst();
    void execute(ChattingMessage serverMessage, ClientMessageType type);
}
