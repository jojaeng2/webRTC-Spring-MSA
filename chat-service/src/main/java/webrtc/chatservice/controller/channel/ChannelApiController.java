package webrtc.chatservice.controller.channel;

import com.sun.istack.NotNull;
import io.swagger.annotations.*;
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
@RequestMapping("/api/v1/webrtc/chat")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ChannelApiController {

    private final ChannelService channelService;

    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/channel")
    public ResponseEntity<CreateChannelResponse> createChannel(@RequestBody CreateChannelRequest request, @RequestHeader("Authorization") String jwtAccessToken) {
        String userEmail = jwtTokenUtil.getUserEmailFromToken(jwtAccessToken.substring(4));
        Channel channel = channelService.createChannel(request, userEmail);
        return new ResponseEntity<>(new CreateChannelResponse(channel.getChannelName(), channel.getLimitParticipants(), channel.getCurrentParticipants(), channel.getTimeToLive()), HttpStatus.OK);
    }

    @GetMapping("/channels/{orderType}/{idx}")
    public ResponseEntity<FindAllChannelResponse> findAnyChannel(@NotNull @PathVariable("orderType") String orderType, @NotNull @PathVariable("idx") String idx) {
        List<ChannelResponse> channels = channelService.findAnyChannel(orderType, Integer.parseInt(idx));
        return new ResponseEntity<>(new FindAllChannelResponse(channels), HttpStatus.OK);
    }

    @GetMapping("/mychannel/{orderType}/{idx}")
    public ResponseEntity<FindAllChannelResponse> findMyAllChannel(@NotNull @PathVariable("orderType") String orderType, @NotNull @RequestHeader("Authorization") String jwtAccessToken, @NotNull @PathVariable("idx") String idx) {
        String userEmail = jwtTokenUtil.getUserEmailFromToken(jwtAccessToken.substring(4));
        List<ChannelResponse> channels = channelService.findMyChannel(orderType, userEmail, Integer.parseInt(idx));
        return new ResponseEntity<>(new FindAllChannelResponse(channels), OK);
    }

    @GetMapping("/channel/{id}")
    public ResponseEntity<ChannelResponse> findOneChannel(@PathVariable("id") String channelId) {
        Channel channel = channelService.findOneChannelById(channelId);
        return new ResponseEntity<>(new ChannelResponse(channelId, channel.getChannelName(), channel.getLimitParticipants(), channel.getCurrentParticipants(), channel.getTimeToLive(), channel.getChannelHashTags(), channel.getChannelType()), OK);
    }
}