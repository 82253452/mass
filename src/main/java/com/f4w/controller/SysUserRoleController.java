package com.f4w.controller;


import com.f4w.annotation.TokenIntecerpt;
import com.f4w.entity.SysUserRole;
import com.f4w.mapper.SysUserRoleMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.MapUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/sysUserRole")
@TokenIntecerpt
public class SysUserRoleController {
    @Resource
    public SysUserRoleMapper sysUserRoleMapper;

    @GetMapping("/selectByPage")
    public PageInfo<SysUserRole> selectByPage(@RequestParam Map map) {
        PageHelper.startPage(MapUtils.getIntValue(map, "page", 1), MapUtils.getIntValue(map, "rows", 10));
        PageInfo<SysUserRole> page = new PageInfo<>(sysUserRoleMapper.selectAll());
        return page;
    }

    @PostMapping("/insert")
    public int insert(@RequestBody SysUserRole SysUserRole) {
        return sysUserRoleMapper.insert(SysUserRole);
    }

    @GetMapping("/selectById")
    public SysUserRole selectById(String id) {
        return sysUserRoleMapper.selectByPrimaryKey(id);
    }

    @PostMapping("/updateById")
    public int updateById(@RequestBody SysUserRole SysUserRole) {
        return sysUserRoleMapper.updateByPrimaryKeySelective(SysUserRole);
    }

    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody String ids) {
        return sysUserRoleMapper.deleteByIds(ids);
    }

    @PostMapping("/deleteById")
    public int deleteById(@RequestBody Map param) {
        return sysUserRoleMapper.deleteByPrimaryKey(MapUtils.getInteger(param, "id"));
    }
}





