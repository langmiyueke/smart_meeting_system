package com.neuedu.mapper;

import com.neuedu.pojo.Employee;
import com.neuedu.pojo.SearchEmployeeRequest;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EmployeeMapper {
    //分页显示员工
    @Select("<script>" +
            "SELECT * " +
            "FROM employee  " +
            "WHERE 1=1" +
            "<if test='enterpriseName != null and enterpriseName != \"\"'>" +
            "  AND enterprise_name=#{enterpriseName} " +
            "</if>" +
            "LIMIT #{pageSize} OFFSET #{offset}" +
            "</script>")
    List<Employee> getEmployeeByPage(@Param("enterpriseName") String enterpriseName, @Param("offset") int offset, @Param("pageSize") int pageSize);

    // 获取总数的方法
    @Select("SELECT COUNT(*) FROM employee where enterprise_name=#{enterpriseName}")
    int getTotalCount(@Param("enterpriseName") String enterpriseName);

    //通过名称，电话，状态，创建时间,页数,公司查找员工
    @Select("<script>" +
            "SELECT * " +
            "FROM employee " +
            "WHERE 1=1" +
            "<if test='request.employee.enterpriseName != null and request.employee.enterpriseName != \"\"'>" +
            "  AND enterprise_name=#{request.employee.enterpriseName} " +
            "</if>" +
            "<if test='request.employee.username != null and request.employee.username != \"\"'>" +
            "  AND username LIKE CONCAT('%', #{request.employee.username}, '%') " +
            "</if>" +
            "<if test='request.employee.phone != null and request.employee.phone != \"\"'>" +
            "  AND phone LIKE CONCAT('%', #{request.employee.phone}, '%') " +
            "</if>" +
            "<if test='request.employee.state != null and request.employee.state != \"\"'>" +
            "  AND state LIKE CONCAT('%', #{request.employee.state}, '%') " +
            "</if>" +
            "<if test='request.end != null and request.end != \"Invalid Date\"'>" +
            "  AND create_at &lt;= #{request.end} " +
            "</if>" +
            "<if test='request.start != null and request.start != \"Invalid Date\"'>" +
            "  AND create_at &gt;= #{request.start} " +
            "</if>" +
            " LIMIT #{pageSize} OFFSET #{offset}" +
            "</script>")
    List<Employee> searchEmployeeByPage(@Param("request") SearchEmployeeRequest request,
                                        @Param("offset") int offset,
                                        @Param("pageSize") int pageSize
    );

    //获取查询后总数
    @Select("<script>" +
            "SELECT COUNT(*) " +
            "FROM employee " +
            "WHERE 1=1" +
            "<if test='request.employee.username != null and request.employee.username != \"\"'>" +
            "  AND username LIKE CONCAT('%', #{request.employee.username}, '%') " +
            "</if>" +
            "<if test='request.employee.phone != null and request.employee.phone != \"\"'>" +
            "  AND phone LIKE CONCAT('%', #{request.employee.phone}, '%') " +
            "</if>" +
            "<if test='request.employee.state != null and request.employee.state != \"\"'>" +
            "  AND state LIKE CONCAT('%', #{request.employee.state}, '%') " +
            "</if>" +
            "<if test='request.end != null and request.end != \"Invalid Date\"'>" +
            "  AND create_at &lt;= #{request.end} " +
            "</if>" +
            "<if test='request.start != null and request.start != \"Invalid Date\"'>" +
            "  AND create_at &gt;= #{request.start} " +
            "</if>" +
            "<if test='request.employee.enterpriseName != null and request.employee.enterpriseName != \"\"'>" +
            "  AND enterprise_name = #{request.employee.enterpriseName} " +
            "</if>" +
            "</script>")
    int getSearchCount(@Param("request") SearchEmployeeRequest request);

    //删除员工信息
    @Delete("DELETE FROM employee " +
            "WHERE id=#{id} ")
    int delEmployee(int id);

    //添加员工信息
    @Insert("INSERT INTO employee (id,username,password,role,enterprise_name,nickname,phone,email,sex,state,department,job,comment,create_at)" +
            "VALUES (null,#{username},#{password},#{role},#{enterpriseName},#{nickname},#{phone},#{email},#{sex},#{state},#{department},#{job},#{comment},#{createAt}) ;")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int addEmployee(Employee employee);


    //修改员工信息
    @Update("UPDATE employee " +
            "SET nickname=#{nickname},phone=#{phone},email=#{email},sex=#{sex},state=#{state},department=#{department},job=#{job},comment=#{comment},role=#{role} " +
            "WHERE id=#{id};"
    )
    int updateEmployee(Employee employee);

    //通过id查找员工信息
    @Select("SELECT * " +
            "FROM employee " +
            "WHERE id=#{id}")
    Employee getEmployee(int id);

    //通过租户的名称查找属于该租户的员工id
    @Select("SELECT id " +
            "FROM employee " +
            "WHERE enterprise_name=#{enterpriseName}")
    List<Integer> getEmployeeIdByEnterpriseName(String enterpriseName);

}
