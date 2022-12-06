package webrtc.v1.hashtag.controller;

import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import webrtc.v1.channel.dto.ChannelDto.FindAllChannelResponse;
import webrtc.v1.channel.entity.Channel;
import webrtc.v1.channel.service.ChannelFindService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/webrtc/chat")
public class HashTagApiController {

    private final ChannelFindService channelFindService;

    @GetMapping("/hashtag/{tagName}/{orderType}/{idx}")
    public ResponseEntity<FindAllChannelResponse> searchHashTag(
            @NotNull @PathVariable("orderType") String type,
            @PathVariable String tagName, @PathVariable("idx") String idx
    ) {
        List<Channel> channels = channelFindService.findByName(tagName, type, Integer.parseInt(idx));
        return new ResponseEntity<>(new FindAllChannelResponse(channels), HttpStatus.OK);
    }
}
