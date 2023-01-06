package webrtc.v1.channel.controller;

import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webrtc.v1.channel.dto.ChannelDto.ChannelResponse;
import webrtc.v1.channel.dto.ChannelDto.CreateChannelRequest;
import webrtc.v1.channel.dto.ChannelDto.CreateChannelResponse;
import webrtc.v1.channel.dto.ChannelDto.FindAllChannelResponse;
import webrtc.v1.channel.entity.Channel;
import webrtc.v1.channel.service.ChannelFindService;
import webrtc.v1.channel.service.ChannelLifeService;
import webrtc.v1.utils.jwt.JwtTokenUtil;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;


@Slf4j
@RestController
@RequestMapping("/api/v1/webrtc/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ChannelApiController {

  private final ChannelLifeService channelLifeService;
  private final ChannelFindService channelFindService;
  private final JwtTokenUtil jwtTokenUtil;

  @PostMapping("/channel")
  public ResponseEntity<CreateChannelResponse> createChannel(
      @RequestBody CreateChannelRequest request,
      @RequestHeader("Authorization") String jwtAccessToken
  ) {
    String userId = getUserId(jwtAccessToken.substring(4));
    Channel channel = channelLifeService.create(request, userId);
    return new ResponseEntity<>(new CreateChannelResponse(channel), HttpStatus.OK);
  }

  @GetMapping("/channels/{orderType}/{idx}")
  public ResponseEntity<FindAllChannelResponse> findAnyChannel(
      @NotNull @PathVariable("orderType") String orderType,
      @NotNull @PathVariable("idx") String idx
  ) {
    List<Channel> channels = channelFindService.findAnyChannel(orderType, Integer.parseInt(idx));
    return new ResponseEntity<>(new FindAllChannelResponse(channels), HttpStatus.OK);
  }

  @GetMapping("/mychannel/{orderType}/{idx}")
  public ResponseEntity<FindAllChannelResponse> findMyAllChannel(
      @NotNull @PathVariable("orderType") String orderType,
      @NotNull @RequestHeader("Authorization") String jwtAccessToken,
      @NotNull @PathVariable("idx") String idx
  ) {
    String userId = getUserId(jwtAccessToken.substring(4));
    List<Channel> channels = channelFindService.findMyChannel(orderType, userId,
        Integer.parseInt(idx));
    return new ResponseEntity<>(new FindAllChannelResponse(channels), OK);
  }

  @GetMapping("/recent/{orderType}/{idx}")
  public ResponseEntity<FindAllChannelResponse> findChannelsRecentlyTalk(
      @NotNull @PathVariable("orderType") String orderType,
      @NotNull @PathVariable("idx") String idx
  ) {
    List<Channel> channels = channelFindService.findChannelsRecentlyTalk(orderType,
        Integer.parseInt(idx));
    return new ResponseEntity<>(new FindAllChannelResponse(channels), OK);
  }


  @GetMapping("/channel/{id}")
  public ResponseEntity<ChannelResponse> findOneChannel(
      @PathVariable("id") String channelId
  ) {
    Channel channel = channelFindService.findById(channelId);
    return new ResponseEntity<>(new ChannelResponse(channel), OK);
  }

  private String getUserId(String token) {
    return jwtTokenUtil.getUserIdFromToken(token);
  }
}
