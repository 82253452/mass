package com.f4w.aop;

import com.f4w.utils.JWTUtils;
import com.f4w.utils.R;
import io.jsonwebtoken.ExpiredJwtException;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


/**
 * @author admin
 */
@Aspect
@Component
public class SysTokenAspect {
    public static final String TOKEN = "token";
    @Resource
    private JWTUtils jwtUtils;

    //    @Pointcut("@annotation(com.f4w.annotation.TokenIntecerpt)")
    @Pointcut("@within(com.f4w.annotation.TokenIntecerpt)")
    public void tokenIntecerpt() {

    }

    @Around(value = "tokenIntecerpt()")
    public Object methodAround(ProceedingJoinPoint pjp) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        String token = request.getHeader("X-Token");
        if (StringUtils.isBlank(token)) {
            return R.error(3000, "jToken为空");
        }
        try {
            jwtUtils.parseBody(token);
        } catch (ExpiredJwtException e) {
            return R.error(3000, "jToken已过期");
        } catch (Exception e) {
            return R.error(3000, "jToken异常");
        }
        return pjp.proceed();
    }

}
