package webrtc.chatservice.controller.channel.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.dto.ChannelDto.*;
import webrtc.chatservice.service.channel.ChannelService;
import webrtc.chatservice.utils.JwtTokenUtil;

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
        String userEmail = jwtTokenUtil.getUserEmailFromToken(jwtAccessToken.substring(4));
        Channel channel = channelService.createChannel(request, userEmail);
        CreateChannelResponse response = new CreateChannelResponse(channel.getChannelName(), channel.getLimitParticipants(), channel.getCurrentParticipants(), channel.getTimeToLive());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/channels/{idx}")
    public ResponseEntity<FindAllChannelResponse> findAllChannel(@PathVariable("idx") String idx) {
        List<ChannelResponse> channels = channelService.findAnyChannel(Integer.parseInt(idx));
        FindAllChannelResponse response = new FindAllChannelResponse(channels);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/mychannel/{idx}")
    public ResponseEntity<FindAllChannelResponse> findMyAllChannel(@RequestHeader("Authorization") String jwtAccessToken, @PathVariable("idx") String idx) {
        String userEmail = jwtTokenUtil.getUserEmailFromToken(jwtAccessToken.substring(4));
        List<ChannelResponse> channels = channelService.findMyChannel(userEmail, Integer.parseInt(idx));
        FindAllChannelResponse response = new FindAllChannelResponse(channels);
        return new ResponseEntity<>(response, OK);
    }

    @GetMapping("/channel/{id}")
    public ResponseEntity<FindOneChannelResponse> findOneChannel(@PathVariable("id") String channelId) {
        Channel channel = channelService.findOneChannelById(channelId);
        FindOneChannelResponse response = new FindOneChannelResponse(channelId, channel.getChannelName(), channel.getLimitParticipants(), channel.getCurrentParticipants(), channel.getTimeToLive());
        return new ResponseEntity<>(response, OK);
    }
}