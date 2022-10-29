package webrtc.chatservice.config.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import webrtc.chatservice.dto.ChannelDto.CreateChannelResponse;
import webrtc.chatservice.dto.chat.ChattingMessage;
import webrtc.chatservice.dto.logstash.LogForCreateChannel;
import webrtc.chatservice.utils.log.LogStashService;

import javax.servlet.http.HttpServletRequest;

@Configuration
@Aspect
@Slf4j
@RequiredArgsConstructor
public class ChannelControllerAopConfig {

    private final LogStashService logStashService;

    @AfterReturning(
            pointcut = "webrtc.chatservice.config.aop.Pointcuts.createChannel()",
            returning = "response"
    )
    public void setLogInfoIfCreateChannelSuccess(JoinPoint joinPoint, CreateChannelResponse response) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = findIP(request.getHeader("X-Forwarded-For"), request);
        String browser = findBrowser(request.getHeader("User-Agent"));
        LogForCreateChannel log = new LogForCreateChannel(ip, "123", "INFO", "POST", browser,
                response.getChannelUsers().get(0).getUser().getId(),
                response.getId(), response.getChannelName(), response.getChannelType());
        logStashService.execute();
    }

    private String findBrowser(String info) {
        String browser = "Unknown";
        if(info.contains("Trident")) browser = "ie";
        else if(info.contains("Edge")) browser = "edge";
        else if(info.contains("Whale")) browser = "whale";
        else if(info.contains("Opera") || info.contains("OPR")) browser = "opera";
        else if(info.contains("Firefox")) browser = "firefox";
        else if(info.contains("Safari") && !info.contains("Chrome")) browser = "safari";
        else if(info.contains("Chrome")) browser = "chrome";
        return browser;
    }

    private String findIP(String ip, HttpServletRequest request) {
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
