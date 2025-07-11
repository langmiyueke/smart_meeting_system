package com.neuedu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neuedu.pojo.Result;
import com.neuedu.service.MeetingService;
import com.neuedu.vo.MeetingVo;
import com.neuedu.vo.PageInfoVo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class MeetingControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private MeetingService meetingService;

    @InjectMocks
    private MeetingController meetingController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(meetingController).build();
    }

    // ============== getMeetings 方法测试 ==============

    // 1. 正常输入 - 带关键字和排序的分页查询
    @Test
    public void getMeetings_NormalCase() throws Exception {
        PageInfoVo normalPageInfo = new PageInfoVo("name", "", 1, 7);
        when(meetingService.getMeetings(any(PageInfoVo.class)))
                .thenReturn(Result.success().data("data", "test"));

        mockMvc.perform(post("/meeting/getMeetings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(normalPageInfo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // 2. 边界值测试 - 最大分页大小
    @Test
    public void getMeetings_MaxPageSize() throws Exception {
        PageInfoVo maxSizePage = new PageInfoVo("", "", 1, 7);
        when(meetingService.getMeetings(any(PageInfoVo.class)))
                .thenReturn(Result.success());

        mockMvc.perform(post("/meeting/getMeetings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(maxSizePage)))
                .andExpect(status().isOk());
    }

    // 3. 边界值测试 - 最小分页大小和页码
    @Test
    public void getMeetings_MinValues() throws Exception {
        PageInfoVo minPage = new PageInfoVo("", "", 1, 1);
        when(meetingService.getMeetings(any(PageInfoVo.class)))
                .thenReturn(Result.success());

        mockMvc.perform(post("/meeting/getMeetings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(minPage)))
                .andExpect(status().isOk());
    }

    // 4. 非法输入 - 分页大小为负数
    @Test
    public void getMeetings_NegativePageSize() throws Exception {
        PageInfoVo invalidPageInfo = new PageInfoVo(1, -1, "搜索词", "排序");

        mockMvc.perform(post("/meeting/getMeetings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPageInfo)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    // 5. 服务层异常处理
    @Test
    public void getMeetings_ServiceException() throws Exception {
        PageInfoVo validPage = new PageInfoVo(1, 10, "正常", "排序");
        when(meetingService.getMeetings(any(PageInfoVo.class)))
                .thenThrow(new RuntimeException("数据库错误"));

        mockMvc.perform(post("/meeting/getMeetings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPage)))
                .andExpect(status().is5xxServerError());
    }

    // ============== sorted 方法测试 ==============

    // 1. 正常输入 - 按名称排序
    @Test
    public void sorted_ByName_Normal() throws Exception {
        when(meetingService.sorted(anyString()))
                .thenReturn((Result) Result.success().getData());

        mockMvc.perform(get("/meeting/sorted")
                        .param("method", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // 2. 正常输入 - 按日期排序
    @Test
    public void sorted_ByDate_Normal() throws Exception {
        when(meetingService.sorted(anyString()))
                .thenReturn(Result.success());

        mockMvc.perform(get("/meeting/sorted")
                        .param("method", "date"))
                .andExpect(status().isOk());
    }

    // 3. 边界值 - 空排序方法
    @Test
    public void sorted_EmptyMethod() throws Exception {
        mockMvc.perform(get("/meeting/sorted")
                        .param("method", ""))
                .andExpect(status().isBadRequest());
    }

    // 4. 非法输入 - 无效排序方法
    @Test
    public void sorted_InvalidMethod() throws Exception {
        mockMvc.perform(get("/meeting/sorted")
                        .param("method", "invalid_sort_method"))
                .andExpect(status().isBadRequest());
    }

    // ============== handleAdd 方法测试 ==============

    // 1. 正常输入 - 有效会议数据
    @Test
    public void handleAdd_NormalCase() throws Exception {
        MeetingVo validMeeting = new MeetingVo();
        validMeeting.setName("季度策略会议");
        validMeeting.setCreator("张经理");
        validMeeting.setContent("讨论Q3季度策略");

        when(meetingService.add(any(MeetingVo.class))).thenReturn(Result.success());

        mockMvc.perform(post("/meeting/handleAdd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validMeeting)))
                .andExpect(status().isOk());
    }

    // 2. 边界值 - 最小数据字段
    @Test
    public void handleAdd_MinimalData() throws Exception {
        MeetingVo minimalMeeting = new MeetingVo();
        minimalMeeting.setName("A"); // 最小名称长度
        minimalMeeting.setCreator("B");
        minimalMeeting.setContent("C");

        when(meetingService.add(any(MeetingVo.class))).thenReturn(Result.success());

        mockMvc.perform(post("/meeting/handleAdd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(minimalMeeting)))
                .andExpect(status().isOk());
    }

    // 3. 非法输入 - 缺失必填字段
    @Test
    public void handleAdd_MissingRequiredFields() throws Exception {
        MeetingVo invalidMeeting = new MeetingVo();
        // 缺少name字段

        mockMvc.perform(post("/meeting/handleAdd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMeeting)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    // 4. 边界值 - 超长字段测试
    @Test
    public void handleAdd_MaxLengthFields() throws Exception {
        MeetingVo longFieldMeeting = new MeetingVo();
        // 创建超长字符串（超过255字符）
        String longString = "a".repeat(256);
        longFieldMeeting.setName(longString);
        longFieldMeeting.setCreator("创建者");

        mockMvc.perform(post("/meeting/handleAdd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(longFieldMeeting)))
                .andExpect(status().isBadRequest());
    }

    // ============== deleteMeeting 方法测试 ==============

    // 1. 正常输入 - 有效ID删除
    @Test
    public void deleteMeeting_ValidId() throws Exception {
        when(meetingService.deleteMeeting(anyLong())).thenReturn(Result.success());

        mockMvc.perform(get("/meeting/deleteMeeting")
                        .param("id", "123"))
                .andExpect(status().isOk());
    }

    // 2. 边界值 - 最小ID值
    @Test
    public void deleteMeeting_MinIdValue() throws Exception {
        when(meetingService.deleteMeeting(1L)).thenReturn(Result.success());

        mockMvc.perform(get("/meeting/deleteMeeting")
                        .param("id", "1"))
                .andExpect(status().isOk());
    }

    // 3. 边界值 - 最大ID值
    @Test
    public void deleteMeeting_MaxIdValue() throws Exception {
        when(meetingService.deleteMeeting(Long.MAX_VALUE)).thenReturn(Result.success());

        mockMvc.perform(get("/meeting/deleteMeeting")
                        .param("id", String.valueOf(Long.MAX_VALUE)))
                .andExpect(status().isOk());
    }

    // 4. 非法输入 - 无效ID格式
    @Test
    public void deleteMeeting_InvalidIdFormat() throws Exception {
        mockMvc.perform(get("/meeting/deleteMeeting")
                        .param("id", "invalid_id"))
                .andExpect(status().isBadRequest());
    }

    // 5. 非法输入 - 负值ID
    @Test
    public void deleteMeeting_NegativeId() throws Exception {
        mockMvc.perform(get("/meeting/deleteMeeting")
                        .param("id", "-1"))
                .andExpect(status().isBadRequest());
    }

    // ============== handleRefactor 方法测试 ==============

    // 1. 正常输入 - 有效更新数据
    @Test
    public void handleRefactor_NormalCase() throws Exception {
        MeetingVo validUpdate = new MeetingVo();
        validUpdate.setId(123);
        validUpdate.setName("更新会议");

        when(meetingService.handleRefactor(any(MeetingVo.class))).thenReturn(Result.success());

        mockMvc.perform(put("/meeting/handleRefactor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUpdate)))
                .andExpect(status().isOk());
    }

    // 2. 边界值 - 更新最小值
    @Test
    public void handleRefactor_MinimalData() throws Exception {
        MeetingVo minUpdate = new MeetingVo();
        minUpdate.setId(1);
        minUpdate.setName("A");

        when(meetingService.handleRefactor(any(MeetingVo.class))).thenReturn(Result.success());

        mockMvc.perform(put("/meeting/handleRefactor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(minUpdate)))
                .andExpect(status().isOk());
    }

    // 3. 非法输入 - 缺少ID
    @Test
    public void handleRefactor_MissingId() throws Exception {
        MeetingVo invalidUpdate = new MeetingVo();
        invalidUpdate.setName("没有ID的会议");

        mockMvc.perform(put("/meeting/handleRefactor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest());
    }

    // 4. 非法输入 - 无效ID格式
    @Test
    public void handleRefactor_InvalidIdFormat() throws Exception {
        // 使用字符串而非数字
        String json = "{\"id\": \"invalid\", \"name\": \"测试\"}";

        mockMvc.perform(put("/meeting/handleRefactor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    // ============== clearIsDeleted 方法测试 ==============

    // 1. 正常调用
    @Test
    public void clearIsDeleted_Normal() throws Exception {
        when(meetingService.clearIsDeleted()).thenReturn(Result.success());

        mockMvc.perform(get("/meeting/clearIsDeleted"))
                .andExpect(status().isOk());
    }

    // 2. 服务异常处理
    @Test
    public void clearIsDeleted_ServiceException() throws Exception {
        when(meetingService.clearIsDeleted())
                .thenThrow(new RuntimeException("数据库错误"));

        mockMvc.perform(get("/meeting/clearIsDeleted"))
                .andExpect(status().is5xxServerError());
    }

    // 3. 错误方法测试 - 使用POST方法
    @Test
    public void clearIsDeleted_WrongMethod() throws Exception {
        mockMvc.perform(post("/meeting/clearIsDeleted"))
                .andExpect(status().isMethodNotAllowed());
    }
}