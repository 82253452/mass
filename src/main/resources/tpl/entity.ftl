package ${package};

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
<#assign dateTime = .now>


/**
 * createBy:${dateTime?string["yyyy-MM-dd HH:mm:ss"]}
 * @author yp
 */
@Table(name = "`${tableClass.tableName}`")
@Data
public class ${tableClass.shortClassName} extends BaseEntity {

<#if tableClass.baseFields??>
    <#list tableClass.baseFields as field>
        <#if field.columnName!='id' && field.columnName!='ctime' && field.columnName!='mtime' && field.columnName!='delete'>
            <#if field.remarks??>
    /**
     * ${field.remarks}
     */
    </#if>
    private ${field.shortTypeName} ${field.fieldName};
        </#if>
    </#list>
</#if>

}