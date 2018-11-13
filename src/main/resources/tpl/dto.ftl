package ${package};

import lombok.Data;
import com.f4w.entity.${tableClass.shortClassName};
<#assign dateTime = .now>

/**
 * createBy:${dateTime?string["yyyy-MM-dd HH:mm:ss"]}
 * @author yp
 */
@Data
public class ${tableClass.shortClassName}Dto extends ${tableClass.shortClassName} {

}