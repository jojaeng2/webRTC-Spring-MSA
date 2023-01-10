package webrtc.v1.utils.jwt.service;

import io.jsonwebtoken.Claims;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtTokenUtil {

  String getUserIdFromToken(String token);

  Date getExpirationDateFromToken(String token);

  <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver);

  Claims getAllClaimsFromToken(String token);

  Boolean isTokenExpired(String token);

  String generateToken(UserDetails userDetails);

  String doGenerateToken(Map<String, Object> claims, String subject);

  Boolean validateToken(String token, UserDetails userDetails);
}
