package webrtc.openvidu.controller.channel.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webrtc.openvidu.domain.channel.Channel;
import webrtc.openvidu.dto.channel.*;
import webrtc.openvidu.service.channel.ChannelService;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/webrtc")
public class ChannelApiController {

    private final ChannelService channelService;

    @PostMapping("/channel")
    public ResponseEntity<CreateChannelResponse> createChannel(@RequestBody CreateChannelRequest request) {
        Channel channel = channelService.createChannel(request);
        CreateChannelResponse response = new CreateChannelResponse(channel.getChannelName(), channel.getLimitParticipants());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/channels")
    public ResponseEntity<FindAllChannelResponse> findAllChannel() {
        List<Channel> channels = channelService.findAllChannel();
        FindAllChannelResponse response = new FindAllChannelResponse(channels);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/channel/{id}")
    public Channel findOneChannel(@PathVariable("id") String channelId) {
        Channel channel = channelService.findOneChannelById(channelId);
        return channel;
    }

    @PostMapping("/channel/leave/{id}")
    public ResponseEntity<LeaveChannelResponse> leaveChannel(@PathVariable("id") String channelId, @RequestBody LeaveChannelRequest request) {

        channelService.leaveChannel(channelId, request.getUserId());
        LeaveChannelResponse response = new LeaveChannelResponse();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
