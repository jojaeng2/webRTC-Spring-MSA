package webrtc.v1.staticgenarator;

import java.sql.Timestamp;
import webrtc.v1.channel.entity.Channel;
import webrtc.v1.channel.enums.ChannelType;

public class ChannelGenerator {

  private static final String name = "테스트채널";
  private static final ChannelType textType = ChannelType.TEXT;
  private static final ChannelType voipType = ChannelType.VOIP;
  private static final Timestamp latestLog = new Timestamp(System.currentTimeMillis());

  private ChannelGenerator() {}

  public static Channel createTextChannel() {
    return Channel.builder()
        .channelName(name)
        .channelType(textType)
        .latestLog(latestLog)
        .build();
  }

  public static Channel createVoipChannel() {
    return Channel.builder()
        .channelName(name)
        .channelType(voipType)
        .latestLog(latestLog)
        .build();
  }

}
