//package webrtc.openvidu.controller.channel;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.stereotype.Controller;
//import webrtc.openvidu.domain.Channel;
//import webrtc.openvidu.dto.chat.ChatServerMessage;
//import webrtc.openvidu.dto.chat.ClientMessage;
//import webrtc.openvidu.dto.chat.ClientMessageType;
//import webrtc.openvidu.dto.chat.ServerMessage;
//import webrtc.openvidu.repository.ChannelRepository;
//import webrtc.openvidu.service.channel.ChannelService;
//import webrtc.openvidu.service.pubsub.RedisPublisher;
//
//import static webrtc.openvidu.enums.SocketServerMessageType.CHAT;
//import static webrtc.openvidu.enums.SocketServerMessageType.RENEWAL;
//
//
//@RequiredArgsConstructor
//@Controller
//public class ChatMessageController {
//
//    private final ChannelService channelService;
//    private final RedisPublisher redisPublisher;
//    private final ChannelRepository channelRepository;
//
//    /**
//     * /pub/chat/room 으로 오는 메시지 반환
//     */
//    @MessageMapping("/chat/room")
//    public void message(ClientMessage message) {
//        ClientMessageType clientMessageType = message.getType();
//        String channelId = message.getChannelId();
//        String clientChatMessage = message.getMessage();
//        Long userId = message.getUserId();
//        switch(clientMessageType) {
//            case ENTER:
//                Channel enterChannel = channelService.findOneChannelById(channelId);
//                ServerMessage enterServerMessage = new ServerMessage(RENEWAL, "입장",channelId, enterChannel.getUsers());
//                redisPublisher.publish(channelRepository.getTopic(channelId), enterServerMessage);
//                break;
//            case LEAVE:
//                Channel leaveChannel = channelService.leaveChannel(channelId, userId);
//                ServerMessage leaveServerMessage = new ServerMessage(RENEWAL, "퇴장", channelId, leaveChannel.getUsers());
//                redisPublisher.publish(channelRepository.getTopic(channelId), leaveServerMessage);
//                break;
//            case CHAT:
//                Channel chatChannel = channelService.findOneChannelById(channelId);
//                ChatServerMessage chatServerMessage = new ChatServerMessage(CHAT, "userName", clientChatMessage);
//                redisPublisher.publish(channelRepository.getTopic(channelId), chatServerMessage);
//                break;
//        }
//    }
//}
