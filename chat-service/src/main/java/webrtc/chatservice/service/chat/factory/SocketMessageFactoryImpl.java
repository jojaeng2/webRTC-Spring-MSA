package webrtc.chatservice.service.chat.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.dto.chat.*;
import webrtc.chatservice.enums.ClientMessageType;
import webrtc.chatservice.service.channel.ChannelIOService;
import webrtc.chatservice.service.chat.template.CreateClientMessageTemplate;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import static webrtc.chatservice.enums.ClientMessageType.*;

@Component
@RequiredArgsConstructor
public class SocketMessageFactoryImpl implements SocketMessageFactory{

    private final ChannelIOService channelIOService;
    private final Map<ClientMessageType, CreateClientMessageTemplate> messageTypes = new HashMap<>();

    @PostConstruct
    public void messageFactoryConst() {
        this.messageTypes.put(CHAT, (type, message, nickname, userId, channelId) -> message.setSenderName(nickname));
        this.messageTypes.put(ENTER, (type, message, nickname, userId, channelId) -> message.setMessage("[알림] " + nickname+ " 님이 채팅방에 입장했습니다."));
        this.messageTypes.put(EXIT, (type, message, nickname, userId, channelId) -> {
            channelIOService.exitChannel(channelId, userId);
            message.setMessage("[알림] " + nickname+ " 님이 채팅방에서 퇴장했습니다.");
        });
    }

    public void execute(ClientMessageType type, ClientMessage overallMessage, Users user, String channelId) {
        this.messageTypes.get(type).build(type, overallMessage, user.getNickname(), user.getId(), channelId);
    }
}
