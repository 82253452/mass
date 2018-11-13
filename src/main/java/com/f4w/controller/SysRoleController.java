package com.f4w.controller;


import com.f4w.annotation.TokenIntecerpt;
import com.f4w.entity.SysRole;
import com.f4w.mapper.SysRoleMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.MapUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/sysRole")
@TokenIntecerpt
public class SysRoleController {
    @Resource
    public SysRoleMapper sysRoleMapper;

    @GetMapping("/selectByPage")
    public PageInfo<SysRole> selectByPage(@RequestParam Map map) {
        PageHelper.startPage(MapUtils.getIntValue(map, "page", 1), MapUtils.getIntValue(map, "rows", 10));
        PageInfo<SysRole> page =new PageInfo<>(sysRoleMapper.selectAll());
    return page;
    }

    @PostMapping("/insert")
    public int insert(@RequestBody SysRole SysRole) {
        return sysRoleMapper.insert(SysRole);
    }

    @GetMapping("/selectById")
    public SysRole selectById(String id) {
        return sysRoleMapper.selectByPrimaryKey(id);
    }

    @PostMapping("/updateById")
    public int updateById(@RequestBody SysRole SysRole) {
        return sysRoleMapper.updateByPrimaryKeySelective(SysRole);
    }

    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody String ids) {
        return sysRoleMapper.deleteByIds(ids);
    }

    @PostMapping("/deleteById")
    public int deleteById(@RequestBody Map param) {
        return sysRoleMapper.deleteByPrimaryKey(MapUtils.getInteger(param, "id"));
    }
}





