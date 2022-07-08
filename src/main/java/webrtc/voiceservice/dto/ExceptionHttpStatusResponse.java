package webrtc.voiceservice.dto;


import lombok.Getter;

@Getter
public class ExceptionHttpStatusResponse {

    private String code;
    private String message;

    public ExceptionHttpStatusResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
