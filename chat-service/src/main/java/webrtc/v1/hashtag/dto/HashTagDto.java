package webrtc.v1.hashtag.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class HashTagDto {

  @Getter
  @AllArgsConstructor
  public static class HashTagResponse {

    private final TagNameResponse hashTag;
  }

  @Getter
  @AllArgsConstructor
  public static class TagNameResponse {

    private final String tagName;
  }
}
