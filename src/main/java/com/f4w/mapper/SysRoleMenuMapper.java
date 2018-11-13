package com.f4w.mapper;

import com.f4w.entity.SysRoleMenu;
import com.f4w.dto.SysRoleMenuDto;
import com.f4w.utils.BaseMapper;
import org.apache.ibatis.annotations.*;

public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

    @Select("select * from sys_role_menu")
    @Results(
            id = "base",
            value = {
                    @Result(property = "id", column = "id"),
                    @Result(property = "roleId", column = "role_id"),
                    @Result(property = "menuId", column = "menu_id"),
                    @Result(property = "ctime", column = "ctime"),
                    @Result(property = "mtime", column = "mtime"),
                    @Result(property = "delete", column = "delete"),
            }
    )
    SysRoleMenu findAll();
    @Select("select * from sys_role_menu")
    @Results(
            id = "baseDto",
            value = {
                    @Result(property = "id", column = "id"),
                    @Result(property = "roleId", column = "role_id"),
                    @Result(property = "menuId", column = "menu_id"),
                    @Result(property = "ctime", column = "ctime"),
                    @Result(property = "mtime", column = "mtime"),
                    @Result(property = "delete", column = "delete"),
            }
    )
    SysRoleMenuDto findDtoAll();

}
