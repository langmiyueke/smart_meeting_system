package com.neuedu.controller;

import com.neuedu.pojo.Result;
import com.neuedu.service.MeetingService;
import com.neuedu.vo.MeetingVo;
import com.neuedu.vo.PageInfoVo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class MeetingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MeetingService meetingService;

    @InjectMocks
    private MeetingController meetingController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(meetingController).build();
    }

    // 测试获取会议分页列表
    @Test
    public void testGetMeetings() throws Exception {
        PageInfoVo pageInfo = new PageInfoVo();
        Result expectedResult = new Result(200, "Success", null);

        Mockito.when(meetingService.getMeetings(pageInfo)).thenReturn(expectedResult);

        mockMvc.perform(post("/meeting/getMeetings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageInfo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // 测试排序会议
    @Test
    public void testSorted() throws Exception {
        String method = "date";
        Result expectedResult = new Result(200, "Sorted", null);

        Mockito.when(meetingService.sorted(method)).thenReturn(expectedResult);

        mockMvc.perform(get("/meeting/sorted?method={method}", method))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // 测试添加会议
    @Test
    public void testHandleAdd() throws Exception {
        MeetingVo meetingVo = new MeetingVo();
        Result expectedResult = new Result(200, "Added", null);

        Mockito.when(meetingService.add(meetingVo)).thenReturn(expectedResult);

        mockMvc.perform(post("/meeting/handleAdd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(meetingVo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // 测试删除会议
    @Test
    public void testDeleteMeeting() throws Exception {
        long id = 1L;
        Result expectedResult = new Result(200, "Deleted", null);

        Mockito.when(meetingService.deleteMeeting(id)).thenReturn(expectedResult);

        mockMvc.perform(get("/meeting/deleteMeeting?id={id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // 测试修改会议
    @Test
    public void testHandleRefactor() throws Exception {
        MeetingVo meetingVo = new MeetingVo();
        meetingVo.setId(1);
        Result expectedResult = new Result(200, "Updated", null);

        Mockito.when(meetingService.handleRefactor(meetingVo)).thenReturn(expectedResult);

        mockMvc.perform(put("/meeting/handleRefactor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(meetingVo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // 测试清理已删除会议
    @Test
    public void testClearIsDeleted() throws Exception {
        Result expectedResult = new Result(200, "Cleared", null);

        Mockito.when(meetingService.clearIsDeleted()).thenReturn(expectedResult);

        mockMvc.perform(get("/meeting/clearIsDeleted"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}