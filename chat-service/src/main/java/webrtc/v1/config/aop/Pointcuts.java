package webrtc.v1.config.aop;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {

    @Pointcut("execution(* webrtc.chatservice.controller..*(..))")
    public void allController() {}

    @Pointcut("execution(* webrtc.chatservice.service..*(..))")
    public void allService() {}

    @Pointcut("execution(* webrtc.chatservice.repository..*(..))")
    public void allRepository() {}

    @Pointcut("allController() || allService() || allRepository()")
    public void controllerAndServiceAndRepository() {}

    @Pointcut("execution(* webrtc.chatservice.controller.createChannel(..))")
    public void createChannel() {}

}