package com.f4w.controller;


import com.f4w.annotation.CurrentUser;
import com.f4w.entity.BusiQuestion;
import com.f4w.entity.SysUser;
import com.f4w.mapper.BusiQuestionMapper;
import com.f4w.utils.R;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.MapUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/busiQuestion")
public class BusiQuestionController {
    @Resource
    public BusiQuestionMapper busiQuestionMapper;

    @GetMapping("/selectByPage")
    public R selectByPage(@CurrentUser SysUser user, @RequestParam Map map) {
        BusiQuestion busiQuestion = new BusiQuestion();
        busiQuestion.setUid(user.getId());
        PageHelper.startPage(MapUtils.getIntValue(map, "page", 1), MapUtils.getIntValue(map, "rows", 10));
        PageInfo<BusiQuestion> page = new PageInfo<>(busiQuestionMapper.select(busiQuestion));
        return R.ok().put("data", page);
    }

    @PostMapping("/insert")
    public R insert(@CurrentUser SysUser user, @RequestBody BusiQuestion BusiQuestion) {
        BusiQuestion.setUid(user.getId());
        int r = busiQuestionMapper.insert(BusiQuestion);
        return R.ok().put("data", r);
    }

    @GetMapping("/selectById")
    public R selectById(String id) {
        BusiQuestion r = busiQuestionMapper.selectByPrimaryKey(id);
        return R.ok().put("data", r);
    }

    @PostMapping("/updateById")
    public R updateById(@RequestBody BusiQuestion BusiQuestion) {
        int r = busiQuestionMapper.updateByPrimaryKeySelective(BusiQuestion);
        return R.ok().put("data", r);
    }

    @PostMapping("/deleteByIds")
    public R deleteByIds(@RequestBody String ids) {
        int r = busiQuestionMapper.deleteByIds(ids);
        return R.ok().put("data", r);
    }

    @PostMapping("/deleteById")
    public R deleteById(@RequestBody Map param) {
        int r = busiQuestionMapper.deleteByPrimaryKey(MapUtils.getInteger(param, "id"));
        return R.ok().put("data", r);
    }
}





