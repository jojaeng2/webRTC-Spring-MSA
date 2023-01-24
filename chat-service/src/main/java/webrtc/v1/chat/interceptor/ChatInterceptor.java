package webrtc.v1.chat.interceptor;


import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import webrtc.v1.utils.client.BrowserParser;
import webrtc.v1.utils.client.IPParser;

@Slf4j
@RequiredArgsConstructor
@Component
public class ChatInterceptor implements HandlerInterceptor {
  private final ObjectMapper objectMapper;
  private final IPParser ipParser;
  private final BrowserParser browserParser;

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    if (request.getClass().getName().contains("SecurityContextHolderAwareRequestWrapper")) return;
    final ContentCachingRequestWrapper cachingRequest = (ContentCachingRequestWrapper) request;
    final ContentCachingResponseWrapper cachingResponse = (ContentCachingResponseWrapper) response;

    String client_ip = ipParser.getIp(cachingRequest);
    String user_agent = browserParser.getBrowser(cachingRequest);
    MDC.put("client_ip", client_ip);
    MDC.put("client_agent", user_agent);

    if (cachingResponse.getContentType() != null && cachingResponse.getContentType().contains("application/json")) {
      if (cachingResponse.getContentAsByteArray() != null && cachingResponse.getContentAsByteArray().length != 0) {
        log.info("Chatting-Log");
      }
    }
  }
}
