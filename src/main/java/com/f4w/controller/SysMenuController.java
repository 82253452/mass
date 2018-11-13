package com.f4w.controller;


import com.f4w.annotation.TokenIntecerpt;
import com.f4w.entity.SysMenu;
import com.f4w.mapper.SysMenuMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.MapUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/sysMenu")
@TokenIntecerpt
public class SysMenuController {
    @Resource
    public SysMenuMapper sysMenuMapper;

    @GetMapping("/selectByPage")
    public PageInfo<SysMenu> selectByPage(@RequestParam Map map) {
        PageHelper.startPage(MapUtils.getIntValue(map, "page", 1), MapUtils.getIntValue(map, "rows", 10));
        PageInfo<SysMenu> page =new PageInfo<>(sysMenuMapper.selectAll());
    return page;
    }

    @PostMapping("/insert")
    public int insert(@RequestBody SysMenu SysMenu) {
        return sysMenuMapper.insert(SysMenu);
    }

    @GetMapping("/selectById")
    public SysMenu selectById(String id) {
        return sysMenuMapper.selectByPrimaryKey(id);
    }

    @PostMapping("/updateById")
    public int updateById(@RequestBody SysMenu SysMenu) {
        return sysMenuMapper.updateByPrimaryKeySelective(SysMenu);
    }

    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody String ids) {
        return sysMenuMapper.deleteByIds(ids);
    }

    @PostMapping("/deleteById")
    public int deleteById(@RequestBody Map param) {
        return sysMenuMapper.deleteByPrimaryKey(MapUtils.getInteger(param, "id"));
    }
}





