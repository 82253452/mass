package com.f4w.controller;


import com.f4w.annotation.CurrentUser;
import com.f4w.annotation.TokenIntecerpt;
import com.f4w.entity.BusiApp;
import com.f4w.entity.BusiArticle;
import com.f4w.entity.SysUser;
import com.f4w.mapper.BusiAppMapper;
import com.f4w.mapper.BusiArticleMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.MapUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author admin
 */
@RestController
@RequestMapping("/busiArticle")
@TokenIntecerpt
public class BusiArticleController {
    @Resource
    private BusiArticleMapper busiArticleMapper;
    @Resource
    private BusiAppMapper busiAppMapper;

    @GetMapping("/selectByPage")
    public PageInfo<BusiArticle> selectByPage(@CurrentUser SysUser sysUser, @RequestParam Map map) {
        BusiArticle busiArticle = new BusiArticle();
        busiArticle.setUid(sysUser.getId());
        PageHelper.startPage(MapUtils.getIntValue(map, "page", 1), MapUtils.getIntValue(map, "rows", 10));
        PageInfo<BusiArticle> page = new PageInfo<>(busiArticleMapper.select(busiArticle));
        return page;
    }

    @GetMapping("/getApps")
    public Map getApps(@CurrentUser SysUser sysUser, @RequestParam Integer type) {
        BusiApp busiApp = new BusiApp();
        busiApp.setUid(sysUser.getId());
        if (type != null) {
            busiApp.setMiniProgramInfo(type);
        }
        List<BusiApp> list = busiAppMapper.select(busiApp);
        Map map = new HashMap();
        list.forEach(e -> {
            map.put(e.getAppId(), e.getNickName());
        });
        return map;
    }


    @PostMapping("/insert")
    public int insert(@CurrentUser SysUser sysUser, @RequestBody BusiArticle busiArticle) {
        busiArticle.setUid(sysUser.getId());
        return busiArticleMapper.insert(busiArticle);
    }

    @GetMapping("/selectById")
    public BusiArticle selectById(String id) {
        return busiArticleMapper.selectByPrimaryKey(id);
    }

    @PostMapping("/updateById")
    public int updateById(@RequestBody BusiArticle BusiArticle) {
        return busiArticleMapper.updateByPrimaryKeySelective(BusiArticle);
    }

    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody String ids) {
        return busiArticleMapper.deleteByIds(ids);
    }

    @PostMapping("/deleteById")
    public int deleteById(@RequestBody Map param) {
        return busiArticleMapper.deleteByPrimaryKey(MapUtils.getInteger(param, "id"));
    }
}





