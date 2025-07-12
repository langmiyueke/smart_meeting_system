package com.neuedu.mapper;

import com.neuedu.pojo.Enterprise;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EnterpriseMapper {

    //显示所有租户
    @Select("select * from enterprise " +
            "LIMIT #{pageSize} OFFSET #{offset}")
    List<Enterprise> getEnterprise(@Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(*) FROM enterprise")
    int getTotalCount();

    //通过标识，联系人，电话，名称查找租户
    @Select("select * from enterprise where enterprise_mark like CONCAT('%', #{enterprises.enterpriseMark}, '%') " +
            "and contact_person like CONCAT('%', #{enterprises.contactPerson}, '%') " +
            "and phone like CONCAT('%', #{enterprises.phone}, '%') " +
            "and name like CONCAT('%', #{enterprises.name}, '%') " +
            "LIMIT #{pageSize} OFFSET #{offset}")
    List<Enterprise> searchEnterprise(@Param("enterprises") Enterprise enterprise,
                                      @Param("offset") int offset,
                                      @Param("pageSize") int pageSize);
    //获取查询后总数
    @Select("select count(*) from enterprise where enterprise_mark like CONCAT('%', #{enterprises.enterpriseMark}, '%') " +
            "and contact_person like CONCAT('%', #{enterprises.contactPerson}, '%') " +
            "and phone like CONCAT('%', #{enterprises.phone}, '%') " +
            "and name like CONCAT('%', #{enterprises.name}, '%') ")
    int getSearchCount(@Param("enterprises") Enterprise enterprise);

    //删除租户信息
    @Delete("delete from enterprise where enterprise_mark=#{enterpriseMark}")
    int delEnterprise(String enterpriseMark);

    //添加租户信息
    @Insert("insert into enterprise values(#{enterpriseMark},#{name},#{contactPerson}," +
            "#{phone},#{managerUsername},#{enterpriseIcon},#{comment})")
    int addEnterprise(Enterprise enterprise);

    //修改租户信息
    @Update("update enterprise set name=#{name},contact_person=#{contactPerson},phone=#{phone}" +
            ",manager_username=#{managerUsername},enterprise_icon=#{enterpriseIcon},comment=#{comment} " +
            " where enterprise_mark=#{enterpriseMark}")
    int updateEnterprise(Enterprise enterprise);

    //通过租户标识获取数据
    @Select("select * from enterprise where enterprise_mark = #{enterpriseMark}")
    Enterprise getEnterpriseByEnterpriseMark(String enterpriseMark);

    //获取租户名称
    @Select("select name from enterprise")
    List<String> getEnterpriseName();

    //通过名称查询租户
    @Select("select name from enterprise where name like CONCAT('%',#{name},'%')")
    List<String> getEnterpriseByName(@Param("name") String name);

}
