package cn.com.edtechhub.workcollaborativeimages.aop;

import cn.com.edtechhub.workcollaborativeimages.annotation.LogParams;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 检查服务调用前的参数情况切面
 */
@Aspect
@Component
@Slf4j
public class LogParamsAOP {

    @Around("@annotation(logParams)")
    public Object logMethodParams(ProceedingJoinPoint joinPoint, LogParams logParams) throws Throwable {
        log.debug(">>> [LogParams] 执行前");
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        StringBuilder sb = new StringBuilder();
        sb
                .append("类名: ").append(className).append("\n")
                .append("方法: ").append(methodName).append("\n")
                .append("过程: ").append("\n")
        ;
        for (int i = 0; i < args.length; i++) {
            sb.append("--> [").append(i).append("]: ").append(args[i]).append("\n");
        }
        Object result = joinPoint.proceed();
        log.debug("服务调用过程检查\n{}==> {}", sb, result);
        log.debug("<<< [LogParams] 执行后");
        return result; // 调用原方法
    }
}
