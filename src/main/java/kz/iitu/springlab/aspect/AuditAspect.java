package kz.iitu.springlab.aspect;

import java.time.Instant;
import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import kz.iitu.springlab.audit.Audited;

@Aspect
@Component
@Order(1) // lowest value = highest precedence: first on the way in, last on the way out
public class AuditAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditAspect.class);

    // "audited" in the expression is bound to the method parameter of the same name
    @Around("@annotation(audited)")
    public Object audit(ProceedingJoinPoint pjp, Audited audited) throws Throwable {
        String args = audited.logArguments() ? " args=" + Arrays.toString(pjp.getArgs()) : "";
        log.info("[AUDIT] start {} at {}{}", audited.action(), Instant.now(), args);
        try {
            Object result = pjp.proceed();
            log.info("[AUDIT] {} success at {}", audited.action(), Instant.now());
            return result;
        } catch (Throwable ex) {
            log.warn("[AUDIT] {} failure at {}: {}", audited.action(), Instant.now(), ex.getMessage());
            throw ex; // do NOT swallow the exception
        }
    }
}
