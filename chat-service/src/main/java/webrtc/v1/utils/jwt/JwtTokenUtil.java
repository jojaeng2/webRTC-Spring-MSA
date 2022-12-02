package webrtc.v1.utils.jwt;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

public interface JwtTokenUtil {

    String getUserEmailFromToken(String token);

    Date getExpirationDateFromToken(String token);

    <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver);

    Claims getAllClaimsFromToken(String token);

    Boolean isTokenExpired(String token);

    String generateToken(UserDetails userDetails);

    String doGenerateToken(Map<String, Object> claims, String subject);

    Boolean validateToken(String token, UserDetails userDetails);
}
