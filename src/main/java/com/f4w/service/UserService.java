package com.f4w.service;

import com.f4w.entity.SysMenu;
import com.f4w.mapper.SysUserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserService {
    @Resource
    private SysUserMapper sysUserMapper;
    private List<SysMenu > getMenuByUserId(String userId){
return null;
    }
}
