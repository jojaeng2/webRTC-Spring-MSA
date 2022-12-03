package webrtc.v1.channel.controller;

import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webrtc.v1.channel.entity.Channel;
import webrtc.v1.channel.dto.ChannelDto.ChannelResponse;
import webrtc.v1.channel.dto.ChannelDto.CreateChannelRequest;
import webrtc.v1.channel.dto.ChannelDto.CreateChannelResponse;
import webrtc.v1.channel.dto.ChannelDto.FindAllChannelResponse;
import webrtc.v1.channel.service.ChannelFindService;
import webrtc.v1.channel.service.ChannelLifeService;
import webrtc.v1.utils.jwt.JwtTokenUtil;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;


@RestController
@RequestMapping("/api/v1/webrtc/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ChannelApiController {

    private final ChannelLifeService channelLifeService;
    private final ChannelFindService channelFindService;
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * 채널 생성 API
     */
    @PostMapping("/channel")
    public ResponseEntity<CreateChannelResponse> createChannel(
            @RequestBody CreateChannelRequest request,
            @RequestHeader("Authorization") String jwtAccessToken
    ) {
        String email = jwtTokenUtil.getUserEmailFromToken(jwtAccessToken.substring(4));
        Channel channel = channelLifeService.create(request, email);
        return new ResponseEntity<>(new CreateChannelResponse(channel), HttpStatus.OK);
    }

    /**
     * 조건없이 채널 목록을 불러오는 API
     */
    @GetMapping("/channels/{orderType}/{idx}")
    public ResponseEntity<FindAllChannelResponse> findAnyChannel(
            @NotNull @PathVariable("orderType") String orderType,
            @NotNull @PathVariable("idx") String idx
    ) {
        List<Channel> channels = channelFindService.findAnyChannel(orderType, Integer.parseInt(idx));
        return new ResponseEntity<>(new FindAllChannelResponse(channels), HttpStatus.OK);
    }

    /**
     * 특정 사용자가 입장한 채널 목록을 불러오는 API
     * jwt access token에 들어있는 userEmail로 회원을 구분
     */
    @GetMapping("/mychannel/{orderType}/{idx}")
    public ResponseEntity<FindAllChannelResponse> findMyAllChannel(
            @NotNull @PathVariable("orderType") String orderType,
            @NotNull @RequestHeader("Authorization") String jwtAccessToken,
            @NotNull @PathVariable("idx") String idx
    ) {
        String userEmail = jwtTokenUtil.getUserEmailFromToken(jwtAccessToken.substring(4));
        List<Channel> channels = channelFindService.findMyChannel(orderType, userEmail, Integer.parseInt(idx));
        return new ResponseEntity<>(new FindAllChannelResponse(channels), OK);
    }

    /**
     * 최근 채팅이 보내진 시간을 기준으로 채널 목록을 불러오는 API
     */
    @GetMapping("/recent/{orderType}/{idx}")
    public ResponseEntity<FindAllChannelResponse> findChannelsRecentlyTalk(
            @NotNull @PathVariable("orderType") String orderType,
            @NotNull @PathVariable("idx") String idx
    ) {
        List<Channel> channels = channelFindService.findChannelsRecentlyTalk(orderType, Integer.parseInt(idx));
        return new ResponseEntity<>(new FindAllChannelResponse(channels), OK);
    }


    /**
     * 특정 채널 하나의 정보를 반환하는 API
     */
    @GetMapping("/channel/{id}")
    public ResponseEntity<ChannelResponse> findOneChannel(
            @PathVariable("id") String channelId
    ) {
        Channel channel = channelFindService.findById(channelId);
        return new ResponseEntity<>(new ChannelResponse(channel), OK);
    }
}
