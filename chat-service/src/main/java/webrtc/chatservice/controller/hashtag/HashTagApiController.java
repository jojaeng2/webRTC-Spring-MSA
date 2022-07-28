package webrtc.chatservice.controller.hashtag;

import com.sun.istack.NotNull;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.dto.ChannelDto;
import webrtc.chatservice.dto.ChannelDto.ChannelResponse;
import webrtc.chatservice.dto.HashTagDto.HashTagResponse;
import webrtc.chatservice.service.channel.ChannelService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/webrtc/chat")
public class HashTagApiController {

    private final ChannelService channelService;

    @GetMapping("/hashtag/{tagName}/{orderType}/{idx}")
    public ResponseEntity<HashTagResponse> searchHashTag(@NotNull @PathVariable("orderType") String orderType, @PathVariable String tagName, @PathVariable("idx") String idx) {
        List<ChannelResponse> channels = channelService.findChannelByHashName(tagName, orderType, Integer.parseInt(idx));
        return new ResponseEntity<>(new HashTagResponse(channels), HttpStatus.OK);
    }
}
