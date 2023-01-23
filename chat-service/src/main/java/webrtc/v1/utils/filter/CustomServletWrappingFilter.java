package webrtc.v1.utils.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
@Order(1)
public class CustomServletWrappingFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    ContentCachingRequestWrapper wrappingRequest = new ContentCachingRequestWrapper(request);
    ContentCachingResponseWrapper wrappingResponse = new ContentCachingResponseWrapper(response);

    chain.doFilter(wrappingRequest, wrappingResponse);

    wrappingResponse.copyBodyToResponse();
  }
}
