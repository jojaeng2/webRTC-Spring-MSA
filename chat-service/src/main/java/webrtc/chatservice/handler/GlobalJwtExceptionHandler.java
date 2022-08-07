package webrtc.chatservice.handler;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import webrtc.chatservice.dto.HttpStatusResponse;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalJwtExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    protected ResponseEntity<?> handleUsernameNotFoundExceptionHandler(UsernameNotFoundException e) {
        final HttpStatusResponse httpStatusResponse = new HttpStatusResponse("존재하지 않는 Users token입니다.", e.getMessage());
        return new ResponseEntity<>(httpStatusResponse, UNAUTHORIZED);
    }

    @ExceptionHandler({ExpiredJwtException.class})
    protected ResponseEntity<?> handleExpiredJwtExceptionHandler(ExpiredJwtException e) {
        final HttpStatusResponse httpStatusResponse = new HttpStatusResponse("Jwt Access Token이 만료되었습니다.", e.getMessage());
        return new ResponseEntity<>(httpStatusResponse, UNAUTHORIZED);
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    protected ResponseEntity<?> handleUnsupportedJwtExceptionHandler(UnsupportedJwtException e) {
        final HttpStatusResponse httpStatusResponse = new HttpStatusResponse("올바르지 않은 Jwt Token 형식입니다.", e.getMessage());
        return new ResponseEntity<>(httpStatusResponse, UNAUTHORIZED);
    }

    @ExceptionHandler(MalformedJwtException.class)
    protected ResponseEntity<?> handleMalformedJwtExceptionHandler(MalformedJwtException e) {
        final HttpStatusResponse httpStatusResponse = new HttpStatusResponse("올바르지 않은 Jwt Token 형식입니다.", e.getMessage());
        return new ResponseEntity<>(httpStatusResponse, UNAUTHORIZED);
    }

    @ExceptionHandler(SignatureException.class)
    protected ResponseEntity<?> handleSignatureExceptionHandler(SignatureException e) {
        final HttpStatusResponse httpStatusResponse = new HttpStatusResponse("올바르지 않은 Jwt Sign 형식입니다.", e.getMessage());
        return new ResponseEntity<>(httpStatusResponse, UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<?> handleIllegalArgumentExceptionHandler(IllegalArgumentException e) {
        final HttpStatusResponse httpStatusResponse = new HttpStatusResponse("올바르지 않은 Jwt 생성자 형식입니다.", e.getMessage());
        return new ResponseEntity<>(httpStatusResponse, UNAUTHORIZED);
    }
}
