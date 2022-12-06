package webrtc.v1.point.enums;

import static webrtc.v1.point.enums.PointMessage.JOIN;

public enum WelcomePoint {


    JOIN(PointMessage.JOIN.getMessage(), 10000000);

    private final String message;
    private final int point;


    WelcomePoint(String message, int point) {
        this.message = message;
        this.point = point;
    }

    public String getMessage() {
        return message;
    }

    public int getPoint() {
        return point;
    }
}
