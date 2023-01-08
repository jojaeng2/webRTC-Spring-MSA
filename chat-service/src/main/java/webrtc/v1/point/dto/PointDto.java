package webrtc.v1.point.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PointDto {
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ChannelTTLWithUserPointResponse {

    private long channelTTL;
    private int point;
  }
}
