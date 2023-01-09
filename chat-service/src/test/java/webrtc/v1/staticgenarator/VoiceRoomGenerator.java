package webrtc.v1.staticgenarator;

import webrtc.v1.voice.entity.VoiceRoom;

public class VoiceRoomGenerator {

  private static final String name = "채팅방#1";
  private static final String id = "Voice Room Id";

  private VoiceRoomGenerator() {}

  public static VoiceRoom createVoiceRoom() {
    return VoiceRoom.builder()
        .name(name)
        .id(id)
        .build();
  }

  public static String getName() {
    return name;
  }

  public static String getId() {
    return id;
  }
}
