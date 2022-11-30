package webrtc.v1.controller.hashtag;

import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webrtc.v1.domain.Channel;
import webrtc.v1.dto.HashTagDto.HashTagResponse;
import webrtc.v1.service.channel.ChannelFindService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/webrtc/chat")
public class HashTagApiController {

    private final ChannelFindService channelFindService;

    @GetMapping("/hashtag/{tagName}/{orderType}/{idx}")
    public ResponseEntity<HashTagResponse> searchHashTag(@NotNull @PathVariable("orderType") String orderType, @PathVariable String tagName, @PathVariable("idx") String idx) {
        List<Channel> channels = channelFindService.findChannelByHashName(tagName, orderType, Integer.parseInt(idx));
        return new ResponseEntity<>(new HashTagResponse(channels), HttpStatus.OK);
    }
}
