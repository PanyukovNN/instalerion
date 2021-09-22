package org.union.promoter.kafka.requestaspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.union.common.exception.InUseException;
import org.union.common.exception.TooOftenRequestException;
import org.union.promoter.kafka.Listener;
import org.union.promoter.requestprocessor.useaspect.ProducingChannelUseAspect;
import org.union.promoter.service.RequestHelper;

import static org.union.common.Constants.*;

/**
 * Aspect for logging kafka listener
 */
@Aspect
@Component
@RequiredArgsConstructor
public class ListenerAspect {

    private final Logger logger = LoggerFactory.getLogger(ProducingChannelUseAspect.class);

    private final RequestHelper requestHelper;

    @Around("@annotation(org.union.promoter.kafka.requestaspect.ListenerChecker)")
    public Object logKafkaListener(ProceedingJoinPoint joinPoint) throws Throwable {
        String rawRequest = (String) joinPoint.getArgs()[0];
        Listener listener = (Listener) joinPoint.getTarget();
        String topicName = listener.getTopicName();

        try {
            requestHelper.isOftenRequests(topicName);

            joinPoint.proceed();

            requestHelper.requestFinished(topicName);
        } catch (TooOftenRequestException e) {
            logger.info(String.format(TOO_OFTEN_REQUESTS_ERROR_MSG, topicName));
        } catch (InUseException e) {
            logger.info(e.getMessage());
        } catch (Exception e) {
            logger.error(String.format(ERROR_WHILE_PUBLICATION, rawRequest), e);
        }

        return null;
    }
}
