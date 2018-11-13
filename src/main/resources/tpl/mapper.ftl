package ${package};

import com.f4w.entity.${tableClass.shortClassName};
import com.f4w.dto.${tableClass.shortClassName}Dto;
import com.f4w.utils.BaseMapper;
import org.apache.ibatis.annotations.*;

public interface ${tableClass.shortClassName}Mapper extends BaseMapper<${tableClass.shortClassName}> {

    @Select("select * from ${tableClass.tableName}")
    @Results(
            id = "base",
            value = {
                <#list tableClass.baseFields as field>
                    @Result(property = "${field.fieldName}", column = "${field.columnName}"),
                </#list>
            }
    )
    ${tableClass.shortClassName} findAll();
    @Select("select * from ${tableClass.tableName}")
    @Results(
            id = "baseDto",
            value = {
                <#list tableClass.baseFields as field>
                    @Result(property = "${field.fieldName}", column = "${field.columnName}"),
                </#list>
            }
    )
    ${tableClass.shortClassName}Dto findDtoAll();

}