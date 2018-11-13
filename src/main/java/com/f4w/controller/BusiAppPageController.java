package com.f4w.controller;


import com.f4w.annotation.CurrentUser;
import com.f4w.annotation.TokenIntecerpt;
import com.f4w.entity.BusiAppPage;
import com.f4w.entity.SysUser;
import com.f4w.mapper.BusiAppPageMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.MapUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/busiAppPage")
@TokenIntecerpt
public class BusiAppPageController {
    @Resource
    public BusiAppPageMapper busiAppPageMapper;

    @GetMapping("/selectByPage")
    public PageInfo<BusiAppPage> selectByPage(@CurrentUser SysUser sysUser, @RequestParam Map map) {
        BusiAppPage busiAppPage = new BusiAppPage();
        busiAppPage.setUid(sysUser.getId());
        PageHelper.startPage(MapUtils.getIntValue(map, "page", 1), MapUtils.getIntValue(map, "rows", 10));
        PageInfo<BusiAppPage> page = new PageInfo<>(busiAppPageMapper.select(busiAppPage));
        return page;
    }

    @PostMapping("/insert")
    public int insert(@RequestBody BusiAppPage BusiAppPage) {
        return busiAppPageMapper.insert(BusiAppPage);
    }

    @GetMapping("/selectById")
    public BusiAppPage selectById(String id) {
        return busiAppPageMapper.selectByPrimaryKey(id);
    }

    @PostMapping("/updateById")
    public int updateById(@RequestBody BusiAppPage BusiAppPage) {
        return busiAppPageMapper.updateByPrimaryKeySelective(BusiAppPage);
    }

    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody String ids) {
        return busiAppPageMapper.deleteByIds(ids);
    }

    @PostMapping("/deleteById")
    public int deleteById(@RequestBody Map param) {
        return busiAppPageMapper.deleteByPrimaryKey(MapUtils.getInteger(param, "id"));
    }
}





