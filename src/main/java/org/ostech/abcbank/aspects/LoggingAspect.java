package org.ostech.abcbank.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* org.ostech.abcbank.controllers..*(..))")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getDeclaringTypeName()
            + "." + joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.info("[REQUEST]  {} | Args: {}", methodName, Arrays.toString(args));

        long start = System.currentTimeMillis();
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable ex) {
            log.error("[ERROR]    {} | Exception: {} | Message: {}", methodName,
                ex.getClass().getSimpleName(), ex.getMessage());
            throw ex;
        }
        long duration = System.currentTimeMillis() - start;

        log.info("[RESPONSE] {} | Duration: {}ms | Result: {}", methodName, duration, result);
        return result;
    }
}
