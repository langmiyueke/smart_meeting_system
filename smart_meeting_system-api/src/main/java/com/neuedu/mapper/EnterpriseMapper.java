package com.neuedu.mapper;

import com.neuedu.pojo.Enterprise;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EnterpriseMapper {

    //显示所有租户
    @Select("select * from enterprise ORDER BY enterprise_mark " +
            "LIMIT #{pageSize} OFFSET #{offset}")
    List<Enterprise> getEnterprise(@Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(*) FROM enterprise")
    int getTotalCount();

    //通过标识，联系人，电话，名称查找租户
    @Select("select * from enterprise where enterprise_mark like CONCAT('%', #{enterprises.enterprise_mark}, '%') " +
            "and contact_person like CONCAT('%', #{enterprises.contact_person}, '%') " +
            "and phone like CONCAT('%', #{enterprises.phone}, '%') " +
            "and name like CONCAT('%', #{enterprises.name}, '%') " +
            "LIMIT #{pageSize} OFFSET #{offset}")
    List<Enterprise> searchEnterprise(@Param("enterprises") Enterprise enterprise,
                                      @Param("offset") int offset,
                                      @Param("pageSize") int pageSize);
    //获取查询后总数
    @Select("select count(*) from enterprise where enterprise_mark like CONCAT('%', #{enterprises.enterprise_mark}, '%') " +
            "and contact_person like CONCAT('%', #{enterprises.contact_person}, '%') " +
            "and phone like CONCAT('%', #{enterprises.phone}, '%') " +
            "and name like CONCAT('%', #{enterprises.name}, '%') ")
    int getSearchCount(@Param("enterprises") Enterprise enterprise);

    //删除租户信息
    @Delete("delete from enterprise where enterprise_mark=#{enterprise_mark}")
    int delEnterprise(String enterprise_mark);

    //添加租户信息
    @Insert("insert into enterprise values(#{enterprise_mark},#{name},#{contact_person}," +
            "#{phone},#{manager_username},#{enterprise_icon},#{comment})")
    int addEnterprise(Enterprise enterprise);

    //修改租户信息
    @Update("update enterprise set name=#{name},contact_person=#{contact_person},phone=#{phone}" +
            ",manager_username=#{manager_username},enterprise_icon=#{enterprise_icon},comment=#{comment} " +
            " where enterprise_mark=#{enterprise_mark}")
    int updateEnterprise(Enterprise enterprise);

    //通过租户标识获取数据
    @Select("select * from enterprise where enterprise_mark = #{enterprise_mark}")
    Enterprise getEnterpriseByEnterpriseMark(String enterprise_mark);

    //获取租户名称
    @Select("select name from enterprise")
    List<String> getEnterpriseName();

    //通过名称查询租户
    @Select("select name from enterprise where name like CONCAT('%',#{name},'%')")
    List<String> getEnterpriseByName(@Param("name") String name);

}