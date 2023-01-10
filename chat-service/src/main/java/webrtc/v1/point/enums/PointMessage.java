package webrtc.v1.point.enums;

public enum PointMessage {
  EXTENSION(" 님이 채널 연장에 포인트를 사용했습니다."),
  CREATE(" 님이 채널 생성에 포인트를 사용했습니다.");

  private final String message;

  PointMessage(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
