package com.f4w.controller;


import com.f4w.entity.BusiQuestion;
import com.f4w.mapper.BusiQuestionMapper;
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
    public PageInfo<BusiQuestion> selectByPage(@RequestParam Map map) {
        PageHelper.startPage(MapUtils.getIntValue(map, "page", 1), MapUtils.getIntValue(map, "rows", 10));
        PageInfo<BusiQuestion> page =new PageInfo<>(busiQuestionMapper.selectAll());
    return page;
    }

    @PostMapping("/insert")
    public int insert(@RequestBody BusiQuestion BusiQuestion) {
        return busiQuestionMapper.insert(BusiQuestion);
    }

    @GetMapping("/selectById")
    public BusiQuestion selectById(String id) {
        return busiQuestionMapper.selectByPrimaryKey(id);
    }

    @PostMapping("/updateById")
    public int updateById(@RequestBody BusiQuestion BusiQuestion) {
        return busiQuestionMapper.updateByPrimaryKeySelective(BusiQuestion);
    }

    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody String ids) {
        return busiQuestionMapper.deleteByIds(ids);
    }

    @PostMapping("/deleteById")
    public int deleteById(@RequestBody Map param) {
        return busiQuestionMapper.deleteByPrimaryKey(MapUtils.getInteger(param, "id"));
    }
}





