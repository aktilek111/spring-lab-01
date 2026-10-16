package kz.iitu.springlab.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class Pointcuts {

    // any method of any class in the service package AND its sub-packages
    @Pointcut("within(kz.iitu.springlab.service..*)")
    public void serviceLayer() { }

    // any public method, any return type, any name, any arguments
    @Pointcut("execution(public * *(..))")
    public void publicMethod() { }

    // intersection of the two
    @Pointcut("serviceLayer() && publicMethod()")
    public void serviceOperation() { }
}
