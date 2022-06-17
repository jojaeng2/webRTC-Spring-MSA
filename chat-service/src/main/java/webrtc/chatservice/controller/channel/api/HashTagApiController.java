package webrtc.chatservice.controller.channel.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.dto.HashTagDto.HashTagResponse;
import webrtc.chatservice.service.channel.ChannelService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/webrtc")
public class HashTagApiController {

    private final ChannelService channelService;

    @GetMapping("/hashtag/{tagName}")
    public ResponseEntity<HashTagResponse> searchHashTag(@PathVariable String tagName) {
        List<Channel> channels = channelService.findChannelByHashName(tagName);
        HashTagResponse response = new HashTagResponse(channels);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
