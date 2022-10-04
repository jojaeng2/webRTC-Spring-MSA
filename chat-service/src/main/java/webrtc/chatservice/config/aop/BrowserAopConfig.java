package webrtc.chatservice.config.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import webrtc.chatservice.dto.chat.ChattingMessage;

import javax.servlet.http.HttpServletRequest;

@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class BrowserAopConfig {

    @Before("webrtc.chatservice.config.aop.Pointcuts.rabbitTemplate()")
    public void addHttpServletRequest(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String browser = findBrowser(request.getHeader("User-Agent"));
        ChattingMessage message = (ChattingMessage)joinPoint.getArgs()[0];
        message.setBrowser(browser);
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
}
