package webrtc.v1.point.enums;

public enum PointUnit {
    CREATE_CHANNEL(2),
    EXTENSION_CHANNEL(30 * 60),
    EXTENSION_CHANNEL_POINT(100);

    private final Integer unit;


    PointUnit(Integer unit) {
        this.unit = unit;
    }

    public Integer getUnit() {
        return unit;
    }
}
