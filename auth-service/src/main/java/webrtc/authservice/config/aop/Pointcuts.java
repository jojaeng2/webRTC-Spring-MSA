package webrtc.authservice.config.aop;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {

    @Pointcut("execution(* webrtc.authservice.controller..*(..))")
    public void allController() {}

    @Pointcut("execution(* webrtc.authservice.service..*(..))")
    public void allService() {}

    @Pointcut("execution(* webrtc.authservice.repository..*(..))")
    public void allRepository() {}

    @Pointcut("allController() || allService() || allRepository()")
    public void controllerAndServiceAndRepository() {}
}




