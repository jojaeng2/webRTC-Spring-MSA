package webrtc.chatservice.config.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import webrtc.chatservice.utils.log.callback.LogTemplate;

@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class LogTraceAopConfig {

    private final LogTemplate logTemplate;


    @Before("webrtc.chatservice.config.aop.Pointcuts.controllerAndServiceAndRepositoryAndJwtFilter()")
    public void doBeforeControllerAndServiceAndRepository(JoinPoint joinPoint) {
        log.info("joinPoint Signature = {}", joinPoint.getSignature());
    }

}
