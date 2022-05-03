package webrtc.openvidu.controller.channel.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webrtc.openvidu.domain.Channel;
import webrtc.openvidu.dto.ChannelDto.*;
import webrtc.openvidu.enums.ChannelServiceReturnType;
import webrtc.openvidu.enums.HttpReturnType;
import webrtc.openvidu.exception.ChannelException;
import webrtc.openvidu.exception.ChannelException.AlreadyExistChannelException;
import webrtc.openvidu.service.channel.ChannelService;
import webrtc.openvidu.utils.JwtTokenUtil;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/webrtc")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ChannelApiController {

    private final ChannelService channelService;

    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/channel")
    public ResponseEntity<CreateChannelResponse> createChannel(@RequestBody CreateChannelRequest request, @RequestHeader("Authorization") String jwtAccessToken) {
        String userName = jwtTokenUtil.getUsernameFromToken(jwtAccessToken.substring(4));
        Channel channel = channelService.createChannel(request, userName);
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
    public ResponseEntity<EnterChannelResponse> enterChannel(@PathVariable("id") String channelId, @RequestHeader("Authorization") String jwtAccessToken) {
        String userName = jwtTokenUtil.getUsernameFromToken(jwtAccessToken.substring(4));
        channelService.enterChannel(channelId, userName);
        return new ResponseEntity<>(new EnterChannelResponse("SUCCESS", "채널에 입장합니다."), OK);
    }

    @PostMapping("/channel/exit/{id}")
    public ResponseEntity<?> exitChannel(@PathVariable("id") String channelId, @RequestHeader("Authorization") String jwtAccessToken) {
        String userName = jwtTokenUtil.getUsernameFromToken(jwtAccessToken.substring(4));
        channelService.exitChannel(channelId, userName);
        return new ResponseEntity<>(OK);
    }

}
