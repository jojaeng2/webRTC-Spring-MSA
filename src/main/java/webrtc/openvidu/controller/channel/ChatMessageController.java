package webrtc.openvidu.controller.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import webrtc.openvidu.domain.channel.Channel;
import webrtc.openvidu.dto.channel.EnterChannelResponse;
import webrtc.openvidu.dto.chat.ClientMessage;
import webrtc.openvidu.dto.chat.ClientMessageType;
import webrtc.openvidu.repository.channel.ChannelRepository;
import webrtc.openvidu.service.channel.ChannelService;
import webrtc.openvidu.service.pubsub.RedisPublisher;

import static webrtc.openvidu.dto.channel.EnterChannelResponse.ResponseType.*;

@RequiredArgsConstructor
@Controller
public class ChatMessageController {

    private final ChannelService channelService;
    private final RedisPublisher redisPublisher;
    private final ChannelRepository channelRepository;

    @MessageMapping("/chat/room")
    public void message(ClientMessage message) {
        ClientMessageType clientMessageType = message.getType();
        String channelId = message.getChannelId();
        Long userId = message.getUserId();
        switch(clientMessageType) {
            case ENTER:
                int result = channelService.enterChannel(channelId, userId);
                Channel channel = channelService.findOneChannelById(channelId);
                switch (result) {
                    case 0 :
                        EnterChannelResponse failResponse = new EnterChannelResponse(ENTERFAIL, "인원이 가득찼습니다.", channel);
                        redisPublisher.publishEnterChannelResponse(channelRepository.getTopic(channelId), failResponse);
                    case 1 :
                        EnterChannelResponse successResponse = new EnterChannelResponse(ENTERSUCCESS, "채널 입장에 성공했습니다.", channel);
                        redisPublisher.publishEnterChannelResponse(channelRepository.getTopic(channelId), successResponse);
                    default:
                        EnterChannelResponse serverErrorResponse = new EnterChannelResponse(SERVERERROR, "Server ERROR 500", channel);
                        redisPublisher.publishEnterChannelResponse(channelRepository.getTopic(channelId), serverErrorResponse);
                 }
            case LEAVE:
                channelService.leaveChannel(channelId, userId);
        }
    }
}
