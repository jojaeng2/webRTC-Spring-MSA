package webrtc.chatservice.config.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
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

    @Around("webrtc.chatservice.config.aop.Pointcuts.controllerAndServiceAndRepository()")
    public Object doLogTraceAOP(ProceedingJoinPoint joinPoint) throws Throwable {

        String className = joinPoint.getTarget().getClass().getName(); // 클래스 이름
        String methodName = joinPoint.getSignature().getName();        // 메서드 이름

        return logTemplate.execute(className, methodName, () -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }
}
