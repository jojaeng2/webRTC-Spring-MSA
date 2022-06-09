package webrtc.openvidu.controller.point;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webrtc.openvidu.dto.ChannelDto;
import webrtc.openvidu.dto.ChannelDto.ExtensionChannelTTLRequest;
import webrtc.openvidu.dto.ChannelDto.ExtensionChannelTTLResponse;
import webrtc.openvidu.service.channel.ChannelService;
import webrtc.openvidu.service.point.PointService;
import webrtc.openvidu.utils.JwtTokenUtil;

import static org.springframework.http.HttpStatus.OK;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/webrtc")
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
