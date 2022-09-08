package webrtc.chatservice.controller.channel;

import com.sun.istack.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.dto.ChannelDto.*;
import webrtc.chatservice.service.channel.ChannelFindService;
import webrtc.chatservice.service.channel.ChannelLifeService;
import webrtc.chatservice.utils.jwt.JwtTokenUtil;
import webrtc.chatservice.utils.log.callback.LogTemplate;
import webrtc.chatservice.utils.log.trace.LogTrace;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;


@RestController
@RequestMapping("/api/v1/webrtc/chat")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ChannelApiController {

    private final ChannelLifeService channelLifeService;
    private final ChannelFindService channelFindService;

    private final JwtTokenUtil jwtTokenUtil;

    private final LogTemplate logTemplate;

    public ChannelApiController(ChannelLifeService channelLifeService, ChannelFindService channelFindService, JwtTokenUtil jwtTokenUtil, LogTrace trace) {
        this.channelLifeService = channelLifeService;
        this.channelFindService = channelFindService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.logTemplate = new LogTemplate(trace);
    }

    @PostMapping("/channel")
    public ResponseEntity<CreateChannelResponse> createChannel(@RequestBody CreateChannelRequest request, @RequestHeader("Authorization") String jwtAccessToken) {
        return logTemplate.execute("ChannelApiController.createChannel", () -> {
            String userEmail = jwtTokenUtil.getUserEmailFromToken(jwtAccessToken.substring(4));
            Channel channel = channelLifeService.createChannel(request, userEmail);
            return new ResponseEntity<>(new CreateChannelResponse(channel.getChannelName(), channel.getLimitParticipants(), channel.getCurrentParticipants(), channel.getTimeToLive()), HttpStatus.OK);
        });
    }

    @GetMapping("/channels/{orderType}/{idx}")
    public ResponseEntity<FindAllChannelResponse> findAnyChannel(@NotNull @PathVariable("orderType") String orderType, @NotNull @PathVariable("idx") String idx) {
        return logTemplate.execute("ChannelApiController.findAnyChannel", () -> {
            List<ChannelResponse> channels = channelFindService.findAnyChannel(orderType, Integer.parseInt(idx));
            return new ResponseEntity<>(new FindAllChannelResponse(channels), HttpStatus.OK);
        });
    }

    @GetMapping("/mychannel/{orderType}/{idx}")
    public ResponseEntity<FindAllChannelResponse> findMyAllChannel(@NotNull @PathVariable("orderType") String orderType, @NotNull @RequestHeader("Authorization") String jwtAccessToken, @NotNull @PathVariable("idx") String idx) {
        return logTemplate.execute("ChannelApiController.findMyAllChannel", () -> {
            String userEmail = jwtTokenUtil.getUserEmailFromToken(jwtAccessToken.substring(4));
            List<ChannelResponse> channels = channelFindService.findMyChannel(orderType, userEmail, Integer.parseInt(idx));
            return new ResponseEntity<>(new FindAllChannelResponse(channels), OK);
        });
    }

    @GetMapping("/channel/{id}")
    public ResponseEntity<ChannelResponse> findOneChannel(@PathVariable("id") String channelId) {
        return logTemplate.execute("ChannelApiController.findOneChannel", () -> {
            Channel channel = channelFindService.findOneChannelById(channelId);
            return new ResponseEntity<>(new ChannelResponse(channelId, channel.getChannelName(), channel.getLimitParticipants(), channel.getCurrentParticipants(), channel.getTimeToLive(), channel.getChannelHashTags(), channel.getChannelType()), OK);
        });
    }
}
