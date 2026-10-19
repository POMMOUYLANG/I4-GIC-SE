package edu.itc.se.shop.service;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Aspect that intercepts all public methods in edu.itc.se.shop.service
 * and measures execution time in microseconds.
 */
@Aspect
@Component
public class TimingAspect {

    private static final Logger log = LoggerFactory.getLogger(TimingAspect.class);

    @Around("execution(public * edu.itc.se.shop.service.*.*(..)) && !within(edu.itc.se.shop.service.TimingAspect)")
    public Object profile(ProceedingJoinPoint pjp) throws Throwable {
        long startNanos = System.nanoTime();
        try {
            return pjp.proceed();
        } finally {
            long durationMicros = (System.nanoTime() - startNanos) / 1000;
            log.debug("{}.{}(..) took {} µs",
                    pjp.getTarget().getClass().getSimpleName(),
                    pjp.getSignature().getName(),
                    durationMicros);
        }
    }
}
