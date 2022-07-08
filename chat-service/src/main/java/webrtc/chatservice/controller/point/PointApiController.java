package webrtc.chatservice.controller.point;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webrtc.chatservice.dto.ChannelDto.ExtensionChannelTTLRequest;
import webrtc.chatservice.dto.ChannelDto.ExtensionChannelTTLResponse;
import webrtc.chatservice.service.channel.ChannelService;
import webrtc.chatservice.utils.JwtTokenUtil;

import static org.springframework.http.HttpStatus.OK;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/webrtc/chat")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PointApiController {

    private final ChannelService channelService;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/extension/{id}")
    public ResponseEntity<ExtensionChannelTTLResponse> extensionChannelTTL(@RequestBody ExtensionChannelTTLRequest request, @PathVariable("id") String channelId, @RequestHeader("Authorization")String jwtAccessToken) {
        String userEmail = jwtTokenUtil.getUserEmailFromToken(jwtAccessToken);
        Long requestTTL = request.getRequestTTL();
        channelService.extensionChannelTTL(channelId, userEmail, requestTTL);
        return new ResponseEntity<>(new ExtensionChannelTTLResponse(channelService.findOneChannelById(channelId).getTimeToLive()), OK);
    }
}
