package webrtc.openvidu.enums;

public enum SocketInterceptorErrorType {
    INTERNAL_ERROR,
    JWT_ACCESS_TOKEN_EXPIRED,
    UNSUPPORTED_JWT_ACCESS_TOKEN,
    MALFORMED_JWT_ACCESS_TOKEN,
    SIGNATURE_EXCEPTION,
    NOT_FOUND_USER_BY_JWT_ACCESS_TOKEN,
    ALREADY_FULL_CHANNEL,
    NOT_EXIST_CHANNEL,
    ALREADY_USER_IN_CHANNEL
}
