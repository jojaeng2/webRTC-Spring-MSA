package webrtc.openvidu.controller.channel.api;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.*;
import webrtc.openvidu.domain.channel.dto.CreateChannelResponse;
import webrtc.openvidu.service.channel.ChannelService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/webrtc")
public class ChannelApiController {

    private final ChannelService channelService;

    @PostMapping("/channel")
    public CreateChannelResponse createChannel(@RequestBody String request) {

        CreateChannelRequest
    }
}
