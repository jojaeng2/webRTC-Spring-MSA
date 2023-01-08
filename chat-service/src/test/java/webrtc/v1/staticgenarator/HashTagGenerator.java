package webrtc.v1.staticgenarator;

import webrtc.v1.hashtag.entity.HashTag;

public class HashTagGenerator {

  private static final String name = "해시태그#1";

  private HashTagGenerator() {}

  public static HashTag createHashTag() {
    return HashTag.builder()
        .name(name)
        .build();
  }

}
