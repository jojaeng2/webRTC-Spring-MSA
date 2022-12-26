package webrtc.v1.point.enums;

public enum PointUnit {
    CREATE_CHANNEL(2L),
    EXTENSION_CHANNEL(30L * 60L);

    private final Long unit;

    PointUnit(Long unit) {
        this.unit = unit;
    }

    public Long getUnit() {
        return unit;
    }
}
