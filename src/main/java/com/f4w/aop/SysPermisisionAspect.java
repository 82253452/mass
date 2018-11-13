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
public class SysPermisisionAspect {
    public static final String TOKEN = "token";
    @Resource
    private JWTUtils jwtUtils;

    @Pointcut("@annotation(com.f4w.annotation.RePermission)")
    public void repPermission() {

    }


    @Around(value = "repPermission()")
    public Object repPermission(ProceedingJoinPoint pjp) throws Throwable {
        try {
            R r = permissionIntercept();
            if (!r.isOk()) {
                return r;
            }
        } catch (ExpiredJwtException e) {
            return R.error(3000, "jToken已过期");
        } catch (Exception e) {
            return R.error(3000, "jToken异常");
        }
        return pjp.proceed();
    }

    private R permissionIntercept() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        Map<String, String[]> map = request.getParameterMap();
        if (map.get(TOKEN) == null) {
            return R.error(3000, "jToken不能为空");
        }
        String jToken = map.get(TOKEN)[0];
        if (jToken == null) {
            return R.error(3000, "jToken不能为空");
        }
        return R.ok();
    }
}
