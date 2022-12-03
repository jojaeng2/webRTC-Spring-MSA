package webrtc.v1.config.aop;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {

    @Pointcut("execution(* webrtc.v1..*controller*(..))")
    public void allController() {}

    @Pointcut("execution(* webrtc.v1..*service*(..))")
    public void allService() {}

    @Pointcut("execution(* webrtc.v1..*repository*(..))")
    public void allRepository() {}

    @Pointcut("allController() || allService() || allRepository()")
    public void controllerAndServiceAndRepository() {}

}