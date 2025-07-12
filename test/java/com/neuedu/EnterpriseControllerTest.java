package com.neuedu;

import com.neuedu.controller.EnterpriseController;
import com.neuedu.mapper.EmployeeMapper;
import com.neuedu.mapper.EnterpriseMapper;
import com.neuedu.mapper.UserMapper;
import com.neuedu.pojo.Enterprise;
import com.neuedu.pojo.User;
import com.neuedu.service.EnterpriseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EnterpriseControllerTest {

    @Mock
    private EnterpriseMapper enterpriseMapper;

    @Mock
    private EnterpriseService enterpriseService;

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private EnterpriseController enterpriseController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getEnterprise_Success() {
        // Arrange
        Enterprise ent = new Enterprise();
        ent.setEnterprise_mark("TEST123");
        List<Enterprise> mockList = Arrays.asList(ent);

        when(enterpriseMapper.getEnterprise(anyInt(), anyInt())).thenReturn(mockList);
        when(enterpriseMapper.getTotalCount()).thenReturn(1);

        // Act
        Map<String, Object> result = enterpriseController.getEnterprise(1, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, ((List<?>) result.get("data")).size());
        assertEquals(1, result.get("total"));
    }

    @Test
    void searchEnterprise_Success() {
        // Arrange
        Enterprise ent = new Enterprise();
        ent.setName("Test");
        List<Enterprise> mockList = Arrays.asList(ent);

        when(enterpriseMapper.searchEnterprise(any(), anyInt(), anyInt())).thenReturn(mockList);
        when(enterpriseMapper.getSearchCount(any())).thenReturn(1);

        // Act
        Map<String, Object> result = enterpriseController.searchEnterprise(ent, 1, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, ((List<?>) result.get("data")).size());
    }

    @Test
    void delEnterprise_Success() {
        // Arrange
        Enterprise ent = new Enterprise();
        ent.setId(1L);
        ent.setName("Test");

        when(enterpriseMapper.getEnterpriseByEnterpriseMark(anyString())).thenReturn(ent);
        when(userMapper.deleteUser(anyLong())).thenReturn(1);
        when(employeeMapper.getEmployeeIdByEnterpriseName(anyString())).thenReturn(Arrays.asList(1));
        when(employeeMapper.delEmployee(anyInt())).thenReturn(1);
        when(enterpriseMapper.delEnterprise(anyString())).thenReturn(1);

        // Act
        int result = enterpriseController.delEnterprise("TEST123");

        // Assert
        assertEquals(1, result);
    }

    @Test
    @Transactional
    void addEnterprise_Success() {
        // Arrange
        Enterprise newEnterprise = new Enterprise();
        newEnterprise.setManager_username("newuser");
        newEnterprise.setEnterprise_mark("PASSWORD");
        newEnterprise.setName("New Enterprise");
        newEnterprise.setPhone("1234567890");

        when(userMapper.getCount("newuser")).thenReturn(0);
        when(enterpriseService.addEnterprises(newEnterprise)).thenReturn(1);
        when(userMapper.insertUser(any(User.class))).thenReturn(1);

        // Act
        int result = enterpriseController.addEnterprise(newEnterprise);

        // Assert
        assertEquals(1, result);
    }

    @Test
    void addEnterprise_UserExist() {
        // Arrange
        Enterprise ent = new Enterprise();
        ent.setManager_username("testadmin");

        when(userMapper.getCount(anyString())).thenReturn(1);
        when(enterpriseMapper.addEnterprise(any())).thenReturn(1);
        when(userMapper.insertUser(any())).thenReturn(1);

        Enterprise ent1 = new Enterprise();
        ent1.setManager_username("testadmin");

        // Act
        int result = enterpriseController.addEnterprise(ent1);

        // Assert
        assertEquals(-1, result);
    }

    @Test
    void updateEnterprise_Success() {
        // Arrange
        Enterprise ent = new Enterprise();
        ent.setEnterprise_mark("TEST123");
        ent.setManager_username("000admin");

        when(enterpriseMapper.updateEnterprise(any())).thenReturn(1);
        when(userMapper.updateUser(any())).thenReturn(1);

        // Act
        int result = enterpriseController.updateEnterprises(ent);

        // Assert
        assertEquals(1, result);
    }

    @Test
    void getEnterpriseByEnterpriseMark_Success() {
        // Arrange
        Enterprise ent = new Enterprise();
        ent.setEnterprise_mark("TEST123");
        ent.setEnterprise_icon("base64data");

        when(enterpriseMapper.getEnterpriseByEnterpriseMark(anyString())).thenReturn(ent);

        // Act
        Enterprise result = enterpriseController.getEnterpriseByEnterpriseMark("TEST123");

        // Assert
        assertNotNull(result);
        assertTrue(result.getEnterprise_icon().startsWith("data:image"));
    }

    @Test
    void getEnterpriseByEnterpriseMark_NullIcon() {
        // Arrange
        Enterprise ent = new Enterprise();
        ent.setEnterprise_mark("TEST123");

        when(enterpriseMapper.getEnterpriseByEnterpriseMark(anyString())).thenReturn(ent);

        // Act
        Enterprise result = enterpriseController.getEnterpriseByEnterpriseMark("TEST123");

        // Assert
        assertNotNull(result);
        assertNull(result.getEnterprise_icon());

    }

    @Test
    void getEnterpriseName_Success() {
        // Arrange
        when(enterpriseMapper.getEnterpriseName()).thenReturn(Arrays.asList("Test1", "Test2"));

        // Act
        List<String> result = enterpriseController.getEnterprisesName();

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    void getEnterpriseByName_Success() {
        // Arrange
        when(enterpriseMapper.getEnterpriseByName(anyString())).thenReturn(Arrays.asList("Test"));

        // Act
        List<String> result = enterpriseController.getEnterprisesByName("Test");

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void getEnterprise_InvalidPageParams() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> enterpriseController.getEnterprise(0, 0));
        assertEquals("页码和每页大小必须是正整数", exception.getMessage());
    }

    // 添加更多边界测试
    @Test
    void getEnterprise_NegativePageParams() {
        assertThrows(IllegalArgumentException.class,
                () -> enterpriseController.getEnterprise(-1, -10));
    }

    @Test
    void getEnterprise_ZeroPageSize() {
        assertThrows(IllegalArgumentException.class,
                () -> enterpriseController.getEnterprise(1, 0));
    }

    @Test
    void getEnterprise_ZeroCurrentPage() {
        assertThrows(IllegalArgumentException.class,
                () -> enterpriseController.getEnterprise(0, 10));
    }
}