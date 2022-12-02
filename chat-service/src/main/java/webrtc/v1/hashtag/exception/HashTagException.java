package webrtc.v1.hashtag.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class HashTagException {

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public static class NotExistHashTagException extends RuntimeException {

    }
}
