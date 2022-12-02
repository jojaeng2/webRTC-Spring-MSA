package webrtc.v1.controller.point;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webrtc.v1.domain.Channel;
import webrtc.v1.dto.ChannelDto.ChannelTTLWithUserPointResponse;
import webrtc.v1.dto.ChannelDto.ExtensionChannelTTLRequest;
import webrtc.v1.dto.ChannelDto.ExtensionChannelTTLResponse;
import webrtc.v1.service.channel.ChannelInfoInjectService;
import webrtc.v1.service.channel.ChannelLifeService;
import webrtc.v1.service.user.UsersService;
import webrtc.v1.utils.jwt.JwtTokenUtil;

import static org.springframework.http.HttpStatus.OK;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/webrtc/chat")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PointApiController {

    private final ChannelLifeService channelLifeService;
    private final ChannelInfoInjectService channelInfoInjectService;
    private final JwtTokenUtil jwtTokenUtil;
    private final UsersService usersService;


    @PostMapping("/extension/{id}")
    public ResponseEntity<ExtensionChannelTTLResponse> extensionChannelTTL(
            @RequestBody ExtensionChannelTTLRequest request,
            @PathVariable("id") String channelId,
            @RequestHeader("Authorization") String jwtAccessToken
    ) {
        String email = getEmail(jwtAccessToken.substring(4));
        long ttl = request.getRequestTTL();
        Channel channel = channelLifeService.extension(channelId, email, ttl);
        return new ResponseEntity<>(new ExtensionChannelTTLResponse(channel.getTimeToLive()), OK);
    }

    @GetMapping("/point/{id}")
    public ResponseEntity<?> findUserPoint(
            @PathVariable("id") String channelId,
            @RequestHeader("Authorization") String jwtAccessToken
    ) {
        String email = getEmail(jwtAccessToken.substring(4));
        int point = usersService.findUserPointByEmail(email);
        long ttl = channelInfoInjectService.findTtl(channelId);
        return new ResponseEntity<>(new ChannelTTLWithUserPointResponse(ttl, point), OK);
    }

    String getEmail(String token) {
        return jwtTokenUtil.getUserEmailFromToken(token);
    }
}