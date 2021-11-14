package org.union.promoter.requestprocessor.useaspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.union.common.exception.InUseException;
import org.union.common.model.request.ProducingChannelRequest;
import org.union.common.service.UseContext;

import static org.union.common.Constants.PRODUCING_CHANNEL_IS_BUSY_MSG;

/**
 * Аспект для блокировки одновременного вызова запросов с одинаковым идентификатором канала публикации
 */
@Aspect
@Component
public class ProducingChannelUseAspect {

    @Around("@annotation(org.union.promoter.requestprocessor.useaspect.ProducingChannelUse)")
    public Object setInUseProducingChannel(ProceedingJoinPoint joinPoint) throws Throwable {
        ProducingChannelRequest request = (ProducingChannelRequest) joinPoint.getArgs()[0];

        try {
            if (UseContext.checkInUseAndSet(request.getProducingChannelId())) {
                throw new InUseException(String.format(PRODUCING_CHANNEL_IS_BUSY_MSG, request.getProducingChannelId()));
            }

            return joinPoint.proceed();
        } finally {
            UseContext.release(request.getProducingChannelId());
        }
    }
}
