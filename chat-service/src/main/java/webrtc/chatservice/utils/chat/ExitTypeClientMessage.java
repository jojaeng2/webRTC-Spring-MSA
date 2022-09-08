package webrtc.chatservice.utils.chat;

import lombok.RequiredArgsConstructor;
import webrtc.chatservice.dto.chat.ClientMessage;
import webrtc.chatservice.enums.ClientMessageType;
import webrtc.chatservice.service.channel.ChannelIOService;

@RequiredArgsConstructor
public class ExitTypeClientMessage implements CreateClientMessage{

    private final ChannelIOService channelIOService;

    @Override
    public void build(ClientMessageType type, ClientMessage overallMessage, String nickname, String userId, String channelId) {
        channelIOService.exitChannel(channelId, userId);
        overallMessage.setMessage("[알림]" + nickname+ " 님이 채팅방에서 퇴장했습니다.");
    }
}
