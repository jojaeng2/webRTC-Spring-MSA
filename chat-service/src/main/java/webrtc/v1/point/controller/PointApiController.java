package webrtc.v1.point.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webrtc.v1.channel.entity.Channel;
import webrtc.v1.channel.dto.ChannelDto.ChannelTTLWithUserPointResponse;
import webrtc.v1.channel.dto.ChannelDto.ExtensionChannelTTLRequest;
import webrtc.v1.channel.dto.ChannelDto.ExtensionChannelTTLResponse;
import webrtc.v1.channel.service.ChannelInfoInjectService;
import webrtc.v1.channel.service.ChannelLifeService;
import webrtc.v1.point.service.PointService;
import webrtc.v1.user.service.UsersService;
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
    private final PointService pointService;


    @PostMapping("/extension/{id}")
    public ResponseEntity<ExtensionChannelTTLResponse> extensionChannelTTL(
            @RequestBody ExtensionChannelTTLRequest request,
            @PathVariable("id") String channelId,
            @RequestHeader("Authorization") String jwtAccessToken
    ) {
        String userId = getUserId(jwtAccessToken.substring(4));
        long ttl = request.getRequestTTL();
        Channel channel = channelLifeService.extension(channelId, userId, ttl);
        return new ResponseEntity<>(new ExtensionChannelTTLResponse(channel.getTimeToLive()), OK);
    }

    @GetMapping("/point/{id}")
    public ResponseEntity<?> findUserPoint(
            @PathVariable("id") String channelId,
            @RequestHeader("Authorization") String jwtAccessToken
    ) {
        String userId = getUserId(jwtAccessToken.substring(4));
        int point = pointService.findPointSum(userId);
        long ttl = channelInfoInjectService.findTtl(channelId);
        return new ResponseEntity<>(new ChannelTTLWithUserPointResponse(ttl, point), OK);
    }

    String getUserId(String token) {
        return jwtTokenUtil.getUserIdFromToken(token);
    }
}