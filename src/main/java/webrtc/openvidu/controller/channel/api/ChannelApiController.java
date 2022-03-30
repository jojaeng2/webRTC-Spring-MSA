package webrtc.openvidu.controller.channel.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webrtc.openvidu.domain.Channel;
import webrtc.openvidu.dto.channel.*;
import webrtc.openvidu.enums.ChannelServiceReturnType;
import webrtc.openvidu.enums.HttpReturnType;
import webrtc.openvidu.service.channel.ChannelService;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/webrtc")
public class ChannelApiController {

    private final ChannelService channelService;

    @PostMapping("/channel")
    public ResponseEntity<CreateChannelResponse> createChannel(@RequestBody CreateChannelRequest request) {
        Channel channel = channelService.createChannel(request);
        System.out.println("channel = " + channel.getId());
        CreateChannelResponse response = new CreateChannelResponse(HttpReturnType.SUCCESS, channel.getChannelName(), channel.getLimitParticipants(), channel.getCurrentParticipants(), channel.getTimeToLive());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/channels")
    public ResponseEntity<FindAllChannelResponse> findAllChannel() {
        List<Channel> channels = channelService.findAllChannel();
        FindAllChannelResponse response = new FindAllChannelResponse(channels);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/channel/{id}")
    public ResponseEntity<FindOneChannelResponse> findOneChannel(@PathVariable("id") String channelId) {
        Channel channel = channelService.findOneChannelById(channelId);
        FindOneChannelResponse response = new FindOneChannelResponse(channelId, channel.getChannelName(), channel.getLimitParticipants(), channel.getCurrentParticipants(), channel.getTimeToLive());
        return new ResponseEntity<>(response, OK);
    }

    @PostMapping("/channel/enter/{id}")
    public EnterChannelResponse enterChannel(@PathVariable("id") String channelId, @RequestBody EnterChannelRequest request) {
        Long userId = request.getUserId();
        ChannelServiceReturnType result = channelService.enterChannel(channelId, userId);
        switch (result) {
            case SUCCESS:
                return new EnterChannelResponse(HttpReturnType.SUCCESS, "채널 입장에 성공했습니다.");
            case FULLCHANNEL:
                return new EnterChannelResponse(HttpReturnType.FAIL, "채널에 인원이 가득차 입장할 수없습니다.");
            default:
                return new EnterChannelResponse(HttpReturnType.SERVERERROR, "Server Error 500");
        }
    }

}
