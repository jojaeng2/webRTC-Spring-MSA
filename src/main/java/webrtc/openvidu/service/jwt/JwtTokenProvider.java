package webrtc.openvidu.service.jwt;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtTokenProvider {

    // Token은 User name을 기준으로 지급함

    @Value("{spring.jwt.secret}")
    private String secretKey;

    private Long tokenValidMilisecond = 1000L*60*60;

    /**
     * Username으로 JwtToken 생성
     */
    public String generateToken(String name) {
        Date now = new Date();
        return Jwts.builder()
                .setId(name)
                .setIssuedAt(now) // 토큰 발행일자
                .setExpiration(new Date(now.getTime() + tokenValidMilisecond)) // 유효시간 설정
                .signWith(SignatureAlgorithm.HS256, secretKey) // hash algorithm + Secret Key setting
                .compact();
    }

    /**
     * Jwt Token을 복호화하여 name을 얻음
     */
    public String getUserNameFromJwt(String jwt) {
        return getClaims(jwt).getBody().getId();
    }

    /**
     * Jwt Token의 유효성을 체크
     */
    public boolean validateToken(String jwt) {
        return this.getClaims(jwt) != null;
    }

    private Jws<Claims> getClaims(String jwt) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt);
        } catch (SignatureException e) {
            throw e;
        } catch (MalformedJwtException e) {
            throw e;
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (UnsupportedJwtException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }
}
