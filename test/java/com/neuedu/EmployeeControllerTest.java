package com.neuedu;

import com.neuedu.controller.EmployeeController;
import com.neuedu.mapper.EmployeeMapper;
import com.neuedu.pojo.Employee;
import com.neuedu.pojo.SearchEmployeeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EmployeeControllerTest {

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeController employeeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getEmployeeByPage_Success() {
        // Arrange
        Employee emp = new Employee();
        emp.setId(1);
        emp.setEnterprise_name("TestEnterprise");
        emp.setCreate_at(Timestamp.valueOf(LocalDateTime.now()));
        List<Employee> mockList = Arrays.asList(emp);

        when(employeeMapper.getEmployeeByPage(anyString(), anyInt(), anyInt())).thenReturn(mockList);
        when(employeeMapper.getTotalCount(anyString())).thenReturn(1);

        // Act
        Map<String, Object> result = employeeController.getEmployeeByPage("TestEnterprise", 1, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, ((List<?>) result.get("data")).size());
        assertEquals(1, result.get("total"));
        assertEquals(1, result.get("currentPage"));
        assertEquals(10, result.get("pageSize"));
    }

    @Test
    void searchEmployee_Success() {
        // Arrange
        SearchEmployeeRequest request = new SearchEmployeeRequest();
        Employee emp = new Employee();
        emp.setCreate_at(Timestamp.valueOf(LocalDateTime.now()));

        request.setEmployee(emp);

        Employee resultEmp = new Employee();
        resultEmp.setId(1);
        resultEmp.setCreate_at(Timestamp.valueOf(LocalDateTime.now()));
        List<Employee> mockList = Arrays.asList(resultEmp);

        when(employeeMapper.searchEmployeeByPage(any(), anyInt(), anyInt())).thenReturn(mockList);
        when(employeeMapper.getSearchCount(any())).thenReturn(1);

        // Act
        Map<String, Object> result = employeeController.searchEmployee(request, 1, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, ((List<?>) result.get("data")).size());
        assertEquals(1, result.get("total"));
    }

    @Test
    void delEmployee_Success() {
        // Arrange
        when(employeeMapper.delEmployee(anyInt())).thenReturn(1);

        // Act
        int result = employeeController.delEmployee(1);

        // Assert
        assertEquals(1, result);
    }

    @Test
    void addEmployee_Success() {
        // Arrange
        Employee emp = new Employee();
        emp.setUsername("testuser");
        emp.setCreate_at(Timestamp.valueOf(LocalDateTime.now()));
        when(employeeMapper.addEmployee(any())).thenReturn(1);

        // Act
        int result = employeeController.addEmployee(emp);

        // Assert
        assertEquals(1, result);
        assertNotNull(emp.getCreate_at());
    }

    @Test
    void updateEmployee_Success() {
        // Arrange
        Employee emp = new Employee();
        emp.setId(1);
        when(employeeMapper.updateEmployee(any())).thenReturn(1);

        // Act
        int result = employeeController.updateEmployee(emp);

        // Assert
        assertEquals(1, result);
    }

    @Test
    void getEmployeeById_Success() {
        // Arrange
        Employee emp = new Employee();
        emp.setId(1);
        when(employeeMapper.getEmployee(anyInt())).thenReturn(emp);

        // Act
        Employee result = employeeController.getEmployeeById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void getEmployeeByPage_EmptyEnterpriseName() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> employeeController.getEmployeeByPage("", 1, 10));
        assertEquals("Enterprise name cannot be empty", exception.getMessage());
    }

    @Test
    void getEmployeeByPage_InvalidPageParams() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> employeeController.getEmployeeByPage("Test", 0, 0));
        assertEquals("Page parameters must be positive", exception.getMessage());
    }

    @Test
    void getEmployeeByPage_NullEnterpriseName() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> employeeController.getEmployeeByPage(null, 1, 10));
        assertEquals("Enterprise name cannot be empty", exception.getMessage());
    }

}