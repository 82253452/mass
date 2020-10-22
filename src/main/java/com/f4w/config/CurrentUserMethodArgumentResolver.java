package com.f4w.config;


import com.f4w.annotation.CurrentUser;
import com.f4w.aop.SysTokenAspect;
import com.f4w.entity.SysUser;
import com.f4w.mapper.SysUserMapper;
import com.f4w.utils.JWTUtils;
import com.f4w.utils.R;
import com.f4w.utils.Result;
import com.f4w.utils.SystemErrorEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.annotation.Resource;

/**
 * @author admin
 * @date 2018/8/18
 */
@Component
public class CurrentUserMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private SysUserMapper sysUserMapper;
    @Autowired
    private JWTUtils jwtUtils;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(SysUser.class)
                && parameter.hasParameterAnnotation(CurrentUser.class);
    }

    @Nullable
    @Override
    public Object resolveArgument(MethodParameter methodParameter, @Nullable ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, @Nullable WebDataBinderFactory webDataBinderFactory) throws Exception {
        if (!methodParameter.getParameterType().isAssignableFrom(SysUser.class)) {
            return null;
        }
        String jToken = nativeWebRequest.getParameter(SysTokenAspect.TOKEN);
        if (StringUtils.isBlank(jToken)) {
            jToken = nativeWebRequest.getHeader("X-Token");
        }
        if (StringUtils.isBlank(jToken)) {
            return Result.render(SystemErrorEnum.AUTH_TOKEN);
        }
        try {
            Object uid = jwtUtils.parseBody(jToken).get("uid");
            if (uid == null) {
                return Result.render(SystemErrorEnum.AUTH_TOKEN);
            }
            return sysUserMapper.selectByPrimaryKey(uid);
        } catch (Exception e) {
            return Result.render(SystemErrorEnum.AUTH_EXP);
        }
    }
}
