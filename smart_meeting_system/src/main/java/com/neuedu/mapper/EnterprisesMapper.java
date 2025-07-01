package com.neuedu.mapper;

import com.neuedu.entity.Enterprises;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EnterprisesMapper {

    //显示所有租户
    @Select("select * from enterprises " +
            "LIMIT #{pageSize} OFFSET #{offset}")
    List<Enterprises> getEnterprises(@Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(*) FROM enterprises")
    int getTotalCount();

    //通过标识，联系人，电话，名称查找租户
    @Select("select * from enterprises where enterprise_mark like CONCAT('%', #{enterprises.enterprise_mark}, '%') " +
            "and contact_person like CONCAT('%', #{enterprises.contact_person}, '%') " +
            "and phone like CONCAT('%', #{enterprises.phone}, '%') " +
            "and name like CONCAT('%', #{enterprises.name}, '%') " +
            "LIMIT #{pageSize} OFFSET #{offset}")
    List<Enterprises> searchEnterprises(@Param("enterprises") Enterprises enterprises,
                                        @Param("offset") int offset,
                                        @Param("pageSize") int pageSize);
    //获取查询后总数
    @Select("select count(*) from enterprises where enterprise_mark like CONCAT('%', #{enterprises.enterprise_mark}, '%') " +
            "and contact_person like CONCAT('%', #{enterprises.contact_person}, '%') " +
            "and phone like CONCAT('%', #{enterprises.phone}, '%') " +
            "and name like CONCAT('%', #{enterprises.name}, '%') ")
    int getSearchCount(@Param("enterprises") Enterprises enterprises);

    //删除租户信息
    @Delete("delete from enterprises where enterprise_mark=#{enterprise_mark}")
    int delEnterprises(String enterprise_mark);

    //添加租户信息
    @Insert("insert into enterprises values(#{enterprise_mark},#{name},#{contact_person}," +
            "#{phone},#{manager_username},#{enterprise_icon},#{comment})")
    int addEnterprises(Enterprises enterprises);

    //修改租户信息
    @Update("update enterprises set name=#{name},contact_person=#{contact_person},phone=#{phone}" +
            ",manager_username=#{manager_username},enterprise_icon=#{enterprise_icon},comment=#{comment} " +
            " where enterprise_mark=#{enterprise_mark}")
    int updateEnterprises(Enterprises enterprises);

    //通过租户标识获取数据
    @Select("select * from enterprises where enterprise_mark = #{enterprise_mark}")
    Enterprises getEnterprisesByEnterpriseMark(String enterprise_mark);

}
