package ${package};


import com.f4w.entity.${tableClass.shortClassName};
import com.f4w.mapper.${tableClass.shortClassName}Mapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.MapUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/${tableClass.variableName}")
public class ${tableClass.shortClassName}Controller {
    @Resource
    public ${tableClass.shortClassName}Mapper ${tableClass.variableName}Mapper;

    @GetMapping("/selectByPage")
    public PageInfo<${tableClass.shortClassName}> selectByPage(@RequestParam Map map) {
        PageHelper.startPage(MapUtils.getIntValue(map, "page", 1), MapUtils.getIntValue(map, "rows", 10));
        PageInfo<${tableClass.shortClassName}> page =new PageInfo<>(${tableClass.variableName}Mapper.selectAll());
    return page;
    }

    @PostMapping("/insert")
    public int insert(@RequestBody ${tableClass.shortClassName} ${tableClass.shortClassName}) {
        return ${tableClass.variableName}Mapper.insert(${tableClass.shortClassName});
    }

    @GetMapping("/selectById")
    public ${tableClass.shortClassName} selectById(String id) {
        return ${tableClass.variableName}Mapper.selectByPrimaryKey(id);
    }

    @PostMapping("/updateById")
    public int updateById(@RequestBody ${tableClass.shortClassName} ${tableClass.shortClassName}) {
        return ${tableClass.variableName}Mapper.updateByPrimaryKeySelective(${tableClass.shortClassName});
    }

    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody String ids) {
        return ${tableClass.variableName}Mapper.deleteByIds(ids);
    }

    @PostMapping("/deleteById")
    public int deleteById(@RequestBody Map param) {
        return ${tableClass.variableName}Mapper.deleteByPrimaryKey(MapUtils.getInteger(param, "id"));
    }
}





