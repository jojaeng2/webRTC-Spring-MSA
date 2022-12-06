package webrtc.v1.chat.enums;

public enum ChatLogCount {
    LOADING(20);

    private final int count;

    ChatLogCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
