package webrtc.voiceservice.config.aop;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {

    @Pointcut("execution(* webrtc.voiceservice.controller..*(..))")
    public void allController() {}

    @Pointcut("execution(* webrtc.voiceservice.service..*(..))")
    public void allService() {}

    @Pointcut("execution(* webrtc.voiceservice.repository..*(..))")
    public void allRepository() {}

    @Pointcut("allController() || allService() || allRepository()")
    public void controllerAndServiceAndRepository() {}
}




