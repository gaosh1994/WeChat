package com.fq.wechat.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author jifang
 * @since 16/3/4 上午9:26.
 */
@Aspect
@Component
public class AccessLog {

    private static Logger LOGGER = LoggerFactory.getLogger(AccessLog.class);

    @Around("execution(* com.fq.wechat.service.impl.*.*(..))")
    public Object processTime(ProceedingJoinPoint point) throws Throwable {

        long begin = System.currentTimeMillis();

        Object result = point.proceed(point.getArgs());

        long time = System.currentTimeMillis() - begin;

        LOGGER.info("method {} invoke consuming {} ms", point.toShortString(), time);

        return result;
    }
}
