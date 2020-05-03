package org.diceresearch.datasetfetcher.logging;

import net.logstash.logback.argument.StructuredArguments;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component

public class LogMonitor {

    private static final Logger logger = LoggerFactory.getLogger(LogMonitor.class);

    @Before(value = "execution(* org.diceresearch.datasetfetcher.utility..*(..)) ||" +
            " execution(* org.diceresearch.datasetfetcher.web..*(..))")
    public void beforeLogger(JoinPoint joinPoint) {
        logger.debug("Before Call: {}, {}, {}",
                StructuredArguments.kv("class",joinPoint.getThis().getClass().getName()),
                StructuredArguments.kv("method", joinPoint.getSignature().getName()),
                StructuredArguments.kv("arguments", Arrays.toString(joinPoint.getArgs())));
    }

    @After(value = "execution(* org.diceresearch.datasetfetcher.utility..*(..)) ||" +
            " execution(* org.diceresearch.datasetfetcher.web..*(..))")
    public void afterLogger(JoinPoint joinPoint) {
        logger.debug("After Call: {}, {}, {}",
                StructuredArguments.kv("class",joinPoint.getThis().getClass().getName()),
                StructuredArguments.kv("method", joinPoint.getSignature().getName()),
                StructuredArguments.kv("arguments", Arrays.toString(joinPoint.getArgs())));
    }
}
