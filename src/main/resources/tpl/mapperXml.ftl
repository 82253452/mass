<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.f4w.mapper.${tableClass.shortClassName}Mapper">
    <resultMap id="BaseResultMap" type="com.f4w.entity.${tableClass.shortClassName}">
        <#list tableClass.baseFields as field>
        <result column="${field.columnName}" jdbcType="${field.jdbcType}" property="${field.fieldName}"/>
        </#list>
    </resultMap>
    <resultMap id="DtoResultMap" type="com.f4w.dto.${tableClass.shortClassName}Dto" extends="BaseResultMap">

    </resultMap>
</mapper>