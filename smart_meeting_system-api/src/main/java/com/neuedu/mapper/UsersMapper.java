package com.neuedu.mapper;

import com.neuedu.pojo.SearchUserRequest;
import com.neuedu.pojo.Users;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UsersMapper {
    //分页显示用户  u.id,u.username,ui.nickname,ui.department,ui.phone,ui.state,ui.create_at
    @Select("<script>" +
            "SELECT * " +
            "FROM user u " +
            "LEFT JOIN userinformation ui ON u.id = ui.id " +
            "WHERE 1=1" +
            "<if test='enterprise_name != null and enterprise_name != \"\"'>" +
            "  AND u.enterprise_name=#{enterprise_name} " +
            "</if>" +
            "LIMIT #{pageSize} OFFSET #{offset}" +
            "</script>")
    List<Users> getUsersByPage(@Param("enterprise_name") String enterprise_name, @Param("offset") int offset, @Param("pageSize") int pageSize);

    // 获取总数的方法
    @Select("SELECT COUNT(*) FROM user where enterprise_name=#{enterprise_name}")
    int getTotalCount(@Param("enterprise_name") String enterprise_name);

    //通过名称，电话，状态，创建时间,页数,公司查找用户
    @Select("<script>" +
            "SELECT * " +
            "FROM user u " +
            "LEFT JOIN userinformation ui ON u.id = ui.id " +
            "WHERE 1=1" +
            "<if test='request.users.enterprise_name != null and request.users.enterprise_name != \"\"'>" +
            "  AND u.enterprise_name=#{request.users.enterprise_name} " +
            "</if>" +
            "<if test='request.users.username != null and request.users.username != \"\"'>" +
            "  AND u.username LIKE CONCAT('%', #{request.users.username}, '%') " +
            "</if>" +
            "<if test='request.users.phone != null and request.users.phone != \"\"'>" +
            "  AND ui.phone LIKE CONCAT('%', #{request.users.phone}, '%') " +
            "</if>" +
            "<if test='request.users.state != null and request.users.state != \"\"'>" +
            "  AND ui.state LIKE CONCAT('%', #{request.users.state}, '%') " +
            "</if>" +
            "<if test='request.end != null and request.end != \"Invalid Date\"'>" +
            "  AND ui.create_at &lt;= #{request.end} " +
            "</if>" +
            "<if test='request.start != null and request.start != \"Invalid Date\"'>" +
            "  AND ui.create_at &gt;= #{request.start} " +
           "</if>" +
            " LIMIT #{pageSize} OFFSET #{offset}" +
            "</script>")
    List<Users> searchUsersByPage(@Param("request") SearchUserRequest request,
                                  @Param("offset") int offset,
                                  @Param("pageSize") int pageSize
                                  );

    //获取查询后总数
    @Select("<script>" +
            "SELECT COUNT(*) " +
            "FROM user u " +
            "LEFT JOIN userinformation ui ON u.id = ui.id " +
            "WHERE 1=1" +
            "<if test='request.users.username != null and request.users.username != \"\"'>" +
            "  AND u.username LIKE CONCAT('%', #{request.users.username}, '%') " +
            "</if>" +
            "<if test='request.users.phone != null and request.users.phone != \"\"'>" +
            "  AND ui.phone LIKE CONCAT('%', #{request.users.phone}, '%') " +
            "</if>" +
            "<if test='request.users.state != null and request.users.state != \"\"'>" +
            "  AND ui.state LIKE CONCAT('%', #{request.users.state}, '%') " +
            "</if>" +
            "<if test='request.end != null and request.end != \"Invalid Date\"'>" +
            "  AND ui.create_at &lt;= #{request.end} " +
            "</if>" +
            "<if test='request.start != null and request.start != \"Invalid Date\"'>" +
            "  AND ui.create_at &gt;= #{request.start} " +
            "</if>" +
            "<if test='request.users.enterprise_name != null and request.users.enterprise_name != \"\"'>" +
            "  AND u.enterprise_name = #{request.users.enterprise_name} " +
            "</if>" +
            "</script>")
    int getSearchCount(@Param("request") SearchUserRequest request);

    //删除用户信息
    @Delete("DELETE FROM user " +
            "WHERE id=#{id} ")
    int delUsers(int id);

    @Delete("DELETE FROM userinformation " +
            "WHERE id=#{id}")
    int delUserInformation(int id);

    //添加用户信息
    @Insert("INSERT INTO user (id,username,password,role,enterprise_name,enterprise_phone,admin_code)" +
            "VALUES (null,#{username},#{password},#{role},#{enterprise_name},#{enterprise_phone},#{admin_code}) ;")
    int addUsers(Users users);

    @Insert("INSERT INTO userinformation (id, nickname, phone,email,sex,state,department,job,comment,create_at)" +
            "VALUES (null, #{nickname},#{phone},#{email},#{sex},#{state},#{department},#{job},#{comment},#{create_at})")
    int addUserInformation(Users users);

    //修改用户信息
    @Update("UPDATE user " +
            "SET role=#{role} " +
            "WHERE id=#{id};"
            )
    int updateUsers(Users users);

    @Update("UPDATE userinformation " +
            "SET nickname=#{nickname},phone=#{phone},email=#{email},sex=#{sex},state=#{state},department=#{department},job=#{job},comment=#{comment} " +
            "WHERE id=#{id};")
    int updateUserInformation(Users users);


    //通过id查找用户信息
    @Select("SELECT * " +
            "FROM user u " +
            "LEFT JOIN userinformation ui ON u.id = ui.id " +
            "WHERE u.id=#{id}")
    Users getUser(int id);

    //通过租户的名称查找属于该租户的用户
    @Select("SELECT u.id " +
            "FROM user u " +
            "LEFT JOIN userinformation ui ON u.id = ui.id " +
            "WHERE u.enterprise_name=#{enterprise_name}")
    List<Integer> getUserByEnterpriseName(String enterprise_name);

}
