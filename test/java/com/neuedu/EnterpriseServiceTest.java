package com.neuedu;

import com.neuedu.mapper.EnterpriseMapper;
import com.neuedu.pojo.Enterprise;
import com.neuedu.service.EnterpriseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EnterpriseServiceTest {

    @Mock
    private EnterpriseMapper enterpriseMapper;

    @InjectMocks
    private EnterpriseService enterpriseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addEnterprises_Success() {
        // Arrange
        Enterprise ent = new Enterprise();
        when(enterpriseMapper.addEnterprise(any())).thenReturn(1);

        // Act
        int result = enterpriseService.addEnterprises(ent);

        // Assert
        assertEquals(1, result);
        assertNotNull(ent.getEnterprise_mark());
        assertTrue(ent.getEnterprise_mark().length() >= 8 && ent.getEnterprise_mark().length() <= 10);
    }

    @Test
    void generateRandomEnterpriseMark_ValidLength() {
        // Act
        String result = enterpriseService.generateRandomEnterpriseMark();

        // Assert
        assertNotNull(result);
        assertTrue(result.length() >= 8 && result.length() <= 10);
    }

    @Test
    void generateRandomEnterpriseMark_ValidCharacters() {
        // Act
        String result = enterpriseService.generateRandomEnterpriseMark();

        // Assert
        assertTrue(result.matches("^[a-zA-Z0-9]+$"));
    }
}