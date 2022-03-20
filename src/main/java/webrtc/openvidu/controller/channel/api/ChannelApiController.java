package webrtc.openvidu.controller.channel.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webrtc.openvidu.domain.channel.Channel;
import webrtc.openvidu.domain.channel.dto.*;
import webrtc.openvidu.service.channel.ChannelService;

import java.util.List;

import static webrtc.openvidu.domain.channel.dto.EnterChannelResponse.ResponseType.*;

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

    @PostMapping("/channel/enter/{id}")
    public ResponseEntity<EnterChannelResponse> enterChannel(@PathVariable("id") String channelId, @RequestBody EnterChannelRequest request) {
        int result = channelService.enterChannel(channelId, request.getUserId());

        switch (result) {
            case 0 :
                EnterChannelResponse failResponse = new EnterChannelResponse(ENTERFAIL, "인원이 가득찼습니다.");
                return new ResponseEntity<>(failResponse, HttpStatus.OK);
            case 1 :
                EnterChannelResponse successResponse = new EnterChannelResponse(ENTERSUCCESS, "채널 입장에 성공했습니다.");
                return new ResponseEntity<>(successResponse, HttpStatus.OK);
        }
        EnterChannelResponse serverErrorResponse = new EnterChannelResponse(SERVERERROR, "Server ");
        return new ResponseEntity<>(serverErrorResponse, HttpStatus.OK);
    }

    @PostMapping("/channel/leave/{id}")
    public ResponseEntity<LeaveChannelResponse> leaveChannel(@PathVariable("id") String channelId, @RequestBody LeaveChannelRequest request) {
        LeaveChannelResponse response = new LeaveChannelResponse();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
