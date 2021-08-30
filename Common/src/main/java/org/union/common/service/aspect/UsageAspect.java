package org.union.common.service.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.union.common.service.UseContext;

@Aspect
@Component
public class UsageAspect {

    @Around(value = "@annotation(inUseAnnotation)")
    public Object aroundCallAt(ProceedingJoinPoint joinPoint, SetInUse inUseAnnotation) throws Throwable {
        String id = inUseAnnotation.id();
        UseContext.checkInUse(id);

        try {
            UseContext.setInUse(id);

            return joinPoint.proceed();
        } finally {
            UseContext.release(id);
        }
    }
}
