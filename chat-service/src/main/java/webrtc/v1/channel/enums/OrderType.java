package webrtc.v1.channel.enums;

public enum OrderType {
    DESC("desc"),
    ASC("asc");

    private final String type;

    OrderType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
