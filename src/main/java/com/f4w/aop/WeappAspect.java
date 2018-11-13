package com.f4w.aop;

import com.f4w.utils.JWTUtils;
import com.f4w.utils.R;
import io.jsonwebtoken.ExpiredJwtException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;


/**
 * @author admin
 */
@Aspect
@Component
public class WeappAspect {
    public static final String KEY = "appId";

    @Pointcut("@annotation(com.f4w.annotation.WeappIntercept)")
    public void weappIntercept() {

    }

    @Around(value = "weappIntercept()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        Map<String, String[]> map = request.getParameterMap();
        if (map.get(KEY) == null) {
            return R.error(3000, "KEY不能为空");
        }
        String VALUE = map.get(KEY)[0];
        if (VALUE == null) {
            return R.error(3000, "KEY不能为空");
        }
        return pjp.proceed();
    }
}
