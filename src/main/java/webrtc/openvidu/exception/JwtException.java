package webrtc.openvidu.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public class JwtException {

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public static class CustomExpiredJwtException extends RuntimeException {

    }
}
