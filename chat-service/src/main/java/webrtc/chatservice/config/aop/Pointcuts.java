package webrtc.chatservice.config.aop;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {

    @Pointcut("execution(* webrtc.chatservice.controller..*(..))")
    public void allController() {}

    @Pointcut("execution(* webrtc.chatservice.service..*(..))")
    public void allService() {}

    @Pointcut("execution(* webrtc.chatservice.repository..*(..))")
    public void allRepository() {}

    @Pointcut("execution(* webrtc.chatservice.utils.jwt..*(..))")
    public void jwtFilter() {}

    @Pointcut("allController() || allService() || allRepository() || jwtFilter()")
    public void controllerAndServiceAndRepositoryAndJwtFilter() {}
}




