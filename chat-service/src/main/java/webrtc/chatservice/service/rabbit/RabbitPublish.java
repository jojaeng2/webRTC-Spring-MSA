package webrtc.chatservice.service.rabbit;

import webrtc.chatservice.dto.chat.ChattingMessage;
import webrtc.chatservice.enums.ClientMessageType;

public interface RabbitPublish {

    void publishMessage(ChattingMessage serverMessage, ClientMessageType type);
}
