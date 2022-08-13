package webrtc.chatservice.service.rabbit;

import webrtc.chatservice.dto.ChatDto;
import webrtc.chatservice.dto.ChatDto.ChatServerMessage;
import webrtc.chatservice.enums.ClientMessageType;

public interface RabbitPublish {

    void publishMessage(ChatServerMessage serverMessage, ClientMessageType type);
}
