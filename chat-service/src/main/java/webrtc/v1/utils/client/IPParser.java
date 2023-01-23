package webrtc.v1.utils.client;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class IPParser {

  public String getIp(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("HTTP_CLIENT_IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("HTTP_X_FORWARDED_FOR");
    }

    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("X-Real-IP");
    }

    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("X-RealIP");
    }

    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("REMOTE_ADDR");
    }

    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    return ip;
  }
}
