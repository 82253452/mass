package com.f4w.controller;


import com.f4w.annotation.TokenIntecerpt;
import com.f4w.entity.SysRoleMenu;
import com.f4w.mapper.SysRoleMenuMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.MapUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/sysRoleMenu")
@TokenIntecerpt
public class SysRoleMenuController {
    @Resource
    public SysRoleMenuMapper sysRoleMenuMapper;

    @GetMapping("/selectByPage")
    public PageInfo<SysRoleMenu> selectByPage(@RequestParam Map map) {
        PageHelper.startPage(MapUtils.getIntValue(map, "page", 1), MapUtils.getIntValue(map, "rows", 10));
        PageInfo<SysRoleMenu> page =new PageInfo<>(sysRoleMenuMapper.selectAll());
    return page;
    }

    @PostMapping("/insert")
    public int insert(@RequestBody SysRoleMenu SysRoleMenu) {
        return sysRoleMenuMapper.insert(SysRoleMenu);
    }

    @GetMapping("/selectById")
    public SysRoleMenu selectById(String id) {
        return sysRoleMenuMapper.selectByPrimaryKey(id);
    }

    @PostMapping("/updateById")
    public int updateById(@RequestBody SysRoleMenu SysRoleMenu) {
        return sysRoleMenuMapper.updateByPrimaryKeySelective(SysRoleMenu);
    }

    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody String ids) {
        return sysRoleMenuMapper.deleteByIds(ids);
    }

    @PostMapping("/deleteById")
    public int deleteById(@RequestBody Map param) {
        return sysRoleMenuMapper.deleteByPrimaryKey(MapUtils.getInteger(param, "id"));
    }
}





