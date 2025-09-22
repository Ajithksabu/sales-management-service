package com.project.salesmanagement.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("within(com.project.salesmanagement..*)")
    public void appLayer() {
    }

    @Before("appLayer()")
    public void before(JoinPoint jp) {
        log.info("➡️ {}.{} args={}", jp.getSignature().getDeclaringTypeName(),
                jp.getSignature().getName(), jp.getArgs());
    }

    @AfterReturning(pointcut = "appLayer()", returning = "result")
    public void after(JoinPoint jp, Object result) {
        log.info("✅ {}.{} result={}", jp.getSignature().getDeclaringTypeName(),
                jp.getSignature().getName(), result);
    }

    @AfterThrowing(pointcut = "appLayer()", throwing = "ex")
    public void error(JoinPoint jp, Throwable ex) {
        log.error("❌ {}.{} ex={}", jp.getSignature().getDeclaringTypeName(),
                jp.getSignature().getName(), ex.toString());
    }
}
