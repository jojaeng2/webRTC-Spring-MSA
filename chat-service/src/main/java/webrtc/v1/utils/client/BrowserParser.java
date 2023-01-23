package webrtc.v1.utils.client;


import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class BrowserParser {

  public String getBrowser(HttpServletRequest request) {
    String browser = "Unknown";
    String info = request.getHeader("User-Agent");
    if (info.contains("Trident")) {
      browser = "ie";
    } else if (info.contains("Edge")) {
      browser = "edge";
    } else if (info.contains("Whale")) {
      browser = "whale";
    } else if (info.contains("Opera") || info.contains("OPR")) {
      browser = "opera";
    } else if (info.contains("Firefox")) {
      browser = "firefox";
    } else if (info.contains("Safari") && !info.contains("Chrome")) {
      browser = "safari";
    } else if (info.contains("Chrome")) {
      browser = "chrome";
    }
    return browser;
  }
}
