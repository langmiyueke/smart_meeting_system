package com.neuedu.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.mapper.MeetingMapper;
import com.neuedu.pojo.Meeting;
import com.neuedu.pojo.Result;
import com.neuedu.vo.MeetingVo;
import com.neuedu.vo.PageInfoVo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.BeanUtils;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MeetingServiceImplEnhancedTest {

    @Mock
    private MeetingMapper meetingMapper;

    @InjectMocks
    private MeetingServiceImpl meetingService;

    private MeetingVo validMeetingVo;
    private PageInfoVo validPageInfoVo;

    @Before
    public void setUp() {
        // 初始化有效的测试数据
        validMeetingVo = new MeetingVo();
        validMeetingVo.setId(1);
        validMeetingVo.setName("季度策略会议");
        validMeetingVo.setCreator("张经理");
        validMeetingVo.setContent("讨论Q3季度策略");


        validPageInfoVo = new PageInfoVo();
        validPageInfoVo.setPageNum(1);
        validPageInfoVo.setPageSize(10);
        validPageInfoVo.setKeyWords("strategy");
        validPageInfoVo.setSortBy("date");
    }

    // ============== getMeetings 方法测试 ==============

    // 1. 正常输入 - 带关键字和排序的分页查询
    @Test
    public void getMeetings_NormalCase() {
        // 模拟分页结果
        IPage<Map> mockPage = mock(IPage.class);
        when(mockPage.getRecords()).thenReturn(Collections.singletonList(new HashMap<>()));
        when(mockPage.getCurrent()).thenReturn(1L);
        when(mockPage.getSize()).thenReturn(10L);
        when(mockPage.getTotal()).thenReturn(100L);

        when(meetingMapper.selectMyPage(
                any(Page.class),
                eq(validPageInfoVo.getKeyWords()),
                eq(validPageInfoVo.getSortBy())
        )).thenReturn(mockPage);

        // 执行测试
        Result result = meetingService.getMeetings(validPageInfoVo);

        // 验证结果
        assertEquals(Optional.of(200), result.getCode());
        Map<String, Object> data = (Map<String, Object>) result.getData();
        Map<String, Object> pageInfo = (Map<String, Object>) data.get("pageInfo");

        assertEquals(1, pageInfo.get("pageNum"));
        assertEquals(10, pageInfo.get("pageSize"));
        assertEquals(100L, pageInfo.get("totalSize"));
    }

    // 2. 边界值测试 - 最大分页大小
    @Test
    public void getMeetings_MaxPageSize() {
        PageInfoVo maxSizePage = new PageInfoVo();
        maxSizePage.setPageNum(1);
        maxSizePage.setPageSize(1000); // 最大页大小

        // 模拟分页结果
        IPage<Map> mockPage = mock(IPage.class);
        when(mockPage.getRecords()).thenReturn(Collections.emptyList());
        when(mockPage.getCurrent()).thenReturn(1L);
        when(mockPage.getSize()).thenReturn(1000L);

        when(meetingMapper.selectMyPage(any(), any(), any())).thenReturn(mockPage);

        Result result = meetingService.getMeetings(maxSizePage);
        assertEquals(Optional.of(200), result.getCode());
    }

    // 3. 边界值测试 - 最小分页大小和页码
    @Test
    public void getMeetings_MinValues() {
        PageInfoVo minPage = new PageInfoVo();
        minPage.setPageNum(1);
        minPage.setPageSize(1); // 最小页大小

        // 模拟分页结果
        IPage<Map> mockPage = mock(IPage.class);
        when(mockPage.getRecords()).thenReturn(Collections.singletonList(new HashMap<>()));
        when(mockPage.getCurrent()).thenReturn(1L);
        when(mockPage.getSize()).thenReturn(1L);
        when(mockPage.getTotal()).thenReturn(1L);

        when(meetingMapper.selectMyPage(any(), any(), any())).thenReturn(mockPage);

        Result result = meetingService.getMeetings(minPage);
        assertEquals(Optional.of(200), result.getCode());
    }

    // 4. 边界值测试 - 第一页
    @Test
    public void getMeetings_FirstPage() {
        PageInfoVo firstPage = new PageInfoVo();
        firstPage.setPageNum(1);
        firstPage.setPageSize(10);

        // 模拟分页结果
        IPage<Map> mockPage = mock(IPage.class);
        when(mockPage.getRecords()).thenReturn(Arrays.asList(new HashMap<>(), new HashMap<>()));

        when(meetingMapper.selectMyPage(any(), any(), any())).thenReturn(mockPage);

        Result result = meetingService.getMeetings(firstPage);
        assertEquals(Optional.of(200), result.getCode());
    }

    // 5. 边界值测试 - 空结果集
    @Test
    public void getMeetings_EmptyResults() {
        // 模拟空结果
        IPage<Map> mockPage = mock(IPage.class);
        when(mockPage.getRecords()).thenReturn(Collections.emptyList());

        when(meetingMapper.selectMyPage(any(), any(), any())).thenReturn(mockPage);

        Result result = meetingService.getMeetings(validPageInfoVo);
        assertEquals(Optional.of(200), result.getCode());

        Map<String, Object> data = (Map<String, Object>) result.getData();
        Map<String, Object> pageInfo = (Map<String, Object>) data.get("pageInfo");
        List<?> records = (List<?>) pageInfo.get("pageData");

        assertTrue(records.isEmpty());
    }

    // 6. 异常场景 - 数据库查询异常
    @Test(expected = RuntimeException.class)
    public void getMeetings_DatabaseError() {
        when(meetingMapper.selectMyPage(any(), any(), any()))
                .thenThrow(new RuntimeException("Database connection failed"));

        meetingService.getMeetings(validPageInfoVo);
    }

    // ============== sorted 方法测试 ==============

    // 1. 正常输入 - 按名称排序
    @Test
    public void sorted_ByName_Normal() {
        // 准备会议数据
        Meeting meeting1 = new Meeting();
        meeting1.setId(1);
        meeting1.setName("Beta Meeting");

        Meeting meeting2 = new Meeting();
        meeting2.setId(2);
        meeting2.setName("Alpha Meeting");

        List<Meeting> meetings = Arrays.asList(meeting1, meeting2);

        when(meetingMapper.selectList(null)).thenReturn(meetings);

        Result result = meetingService.sorted("name");

        // 验证结果
        MeetingVo[] meetingArray = (MeetingVo[]) result.getData();
        assertEquals("Alpha Meeting", meetingArray[0].getName());
        assertEquals("Beta Meeting", meetingArray[1].getName());
    }

    // 2. 边界值 - 单元素排序
    @Test
    public void sorted_SingleItem() {
        Meeting singleMeeting = new Meeting();
        singleMeeting.setId(1);
        singleMeeting.setName("Solo Meeting");

        when(meetingMapper.selectList(null)).thenReturn(Collections.singletonList(singleMeeting));

        Result result = meetingService.sorted("name");

        MeetingVo[] meetingArray = (MeetingVo[]) result.getData();
        assertEquals(1, meetingArray.length);
        assertEquals("Solo Meeting", meetingArray[0].getName());
    }

    // 3. 边界值 - 空会议列表排序
    @Test
    public void sorted_EmptyList() {
        when(meetingMapper.selectList(null)).thenReturn(Collections.emptyList());

        Result result = meetingService.sorted("name");

        MeetingVo[] meetingArray = (MeetingVo[]) result.getData();
        assertEquals(0, meetingArray.length);
    }

    // 4. 异常场景 - 无效排序方法
    @Test
    public void sorted_InvalidMethod() {
        Meeting meeting = new Meeting();
        meeting.setName("Test Meeting");

        when(meetingMapper.selectList(null)).thenReturn(Collections.singletonList(meeting));

        Result result = meetingService.sorted("invalid_method");

        // 应该使用默认排序（按名称）
        MeetingVo[] meetingArray = (MeetingVo[]) result.getData();
        assertEquals("Test Meeting", meetingArray[0].getName());
    }

    // 5. 边界值 - 大小写敏感排序
    @Test
    public void sorted_CaseSensitive() {
        Meeting meeting1 = new Meeting();
        meeting1.setId(1);
        meeting1.setName("apple");

        Meeting meeting2 = new Meeting();
        meeting2.setId(2);
        meeting2.setName("Banana");

        List<Meeting> meetings = Arrays.asList(meeting1, meeting2);

        when(meetingMapper.selectList(null)).thenReturn(meetings);

        Result result = meetingService.sorted("name");

        MeetingVo[] meetingArray = (MeetingVo[]) result.getData();
        assertEquals("apple", meetingArray[0].getName());
        assertEquals("Banana", meetingArray[1].getName());
    }

    // ============== add 方法测试 ==============

    // 1. 正常输入 - 有效会议数据
    @Test
    public void add_NormalCase() {
        // 模拟insert操作
        doAnswer(invocation -> {
            Meeting meeting = invocation.getArgument(0);
            meeting.setId(100); // 设置生成的ID
            return 1;
        }).when(meetingMapper).insert(any(Meeting.class));

        Result result = meetingService.add(validMeetingVo);

        assertEquals(Optional.of(200), result.getCode());
        verify(meetingMapper, times(1)).insert(any(Meeting.class));
    }

    // 2. 边界值 - 最小数据字段
    @Test
    public void add_MinimalData() {
        MeetingVo minimalMeeting = new MeetingVo();
        minimalMeeting.setName("A");
        minimalMeeting.setCreator("B");
        minimalMeeting.setContent("C");


        doAnswer(invocation -> {
            Meeting meeting = invocation.getArgument(0);
            meeting.setId(101);
            return 1;
        }).when(meetingMapper).insert(any(Meeting.class));

        Result result = meetingService.add(minimalMeeting);
        assertEquals(Optional.of(200), result.getCode());
    }

    // 3. 边界值 - 空描述字段
    @Test
    public void add_EmptyDescription() {
        MeetingVo meeting = new MeetingVo();
        meeting.setName("Empty Desc");
        meeting.setCreator("Admin");


        doAnswer(invocation -> {
            Meeting m = invocation.getArgument(0);

            return 1;
        }).when(meetingMapper).insert(any(Meeting.class));

        Result result = meetingService.add(meeting);
        assertEquals(Optional.of(200), result.getCode());
    }

    // 4. 异常场景 - 插入失败
    @Test
    public void add_InsertFailed() {
        when(meetingMapper.insert(any(Meeting.class))).thenReturn(0);

        Result result = meetingService.add(validMeetingVo);

        assertEquals(Optional.of(500), result.getCode());
        assertTrue(result.getMessage().contains("插入失败"));
    }

    // 5. 异常场景 - 数据库异常
    @Test(expected = RuntimeException.class)
    public void add_DatabaseError() {
        when(meetingMapper.insert(any(Meeting.class)))
                .thenThrow(new RuntimeException("Database error"));

        meetingService.add(validMeetingVo);
    }

    // ============== deleteMeeting 方法测试 ==============

    // 1. 正常输入 - 有效ID删除
    @Test
    public void deleteMeeting_ValidId() {
        Meeting meeting = new Meeting();
        meeting.setId(123);

        when(meetingMapper.selectById(123L)).thenReturn(meeting);
        when(meetingMapper.deleteById(123L)).thenReturn(1);

        Result result = meetingService.deleteMeeting(123L);

        assertEquals(Optional.of(200), result.getCode());
    }

    // 2. 边界值 - 最小ID值
    @Test
    public void deleteMeeting_MinIdValue() {
        Meeting meeting = new Meeting();
        meeting.setId(1);

        when(meetingMapper.selectById(1L)).thenReturn(meeting);
        when(meetingMapper.deleteById(1L)).thenReturn(1);

        Result result = meetingService.deleteMeeting(1L);
        assertEquals(Optional.of(200), result.getCode());
    }

    // 3. 边界值 - 最大ID值
    @Test
    public void deleteMeeting_MaxIdValue() {
        long maxId = Long.MAX_VALUE;
        Meeting meeting = new Meeting();
        meeting.setId((int) maxId);

        when(meetingMapper.selectById(maxId)).thenReturn(meeting);
        when(meetingMapper.deleteById(maxId)).thenReturn(1);

        Result result = meetingService.deleteMeeting(maxId);
        assertEquals(Optional.of(200), result.getCode());
    }

    // 4. 边界值 - 删除不存在的会议
    @Test
    public void deleteMeeting_NonExistingId() {
        when(meetingMapper.selectById(999L)).thenReturn(null);

        Result result = meetingService.deleteMeeting(999L);

        assertEquals(Optional.of(500), result.getCode());
        assertTrue(result.getMessage().contains("课程不存在"));
    }

    // 5. 异常场景 - 删除操作失败
    @Test
    public void deleteMeeting_DeleteFailed() {
        Meeting meeting = new Meeting();
        meeting.setId(100);

        when(meetingMapper.selectById(100L)).thenReturn(meeting);
        when(meetingMapper.deleteById(100L)).thenReturn(0);

        Result result = meetingService.deleteMeeting(100L);

        assertEquals(Optional.of(500), result.getCode());
        assertTrue(result.getMessage().contains("删除失败"));
    }

    // ============== handleRefactor 方法测试 ==============

    // 1. 正常输入 - 有效更新数据
    @Test
    public void handleRefactor_NormalCase() {
        when(meetingMapper.updateById(any(Meeting.class))).thenReturn(1);

        Result result = meetingService.handleRefactor(validMeetingVo);

        assertEquals(Optional.of(200), result.getCode());
        verify(meetingMapper, times(1)).updateById(any(Meeting.class));
    }

    // 2. 边界值 - 部分字段更新
    @Test
    public void handleRefactor_PartialUpdate() {
        MeetingVo partialUpdate = new MeetingVo();
        partialUpdate.setId(1);
        partialUpdate.setName("Updated Name Only");

        when(meetingMapper.updateById(any(Meeting.class))).thenReturn(1);

        Result result = meetingService.handleRefactor(partialUpdate);
        assertEquals(Optional.of(200), result.getCode());
    }

    // 3. 边界值 - 空值处理
    @Test
    public void handleRefactor_NullValues() {
        MeetingVo nullUpdate = new MeetingVo();
        nullUpdate.setId(1);
        nullUpdate.setName(null);
        nullUpdate.setCreator(null);

        when(meetingMapper.updateById(any(Meeting.class))).thenReturn(1);

        Result result = meetingService.handleRefactor(nullUpdate);
        assertEquals(Optional.of(200), result.getCode());
    }

    // 4. 异常场景 - 更新不存在的会议
    @Test
    public void handleRefactor_UpdateNonExisting() {
        when(meetingMapper.updateById(any(Meeting.class))).thenReturn(0);

        Result result = meetingService.handleRefactor(validMeetingVo);

        assertEquals(Optional.of(500), result.getCode());
        assertTrue(result.getMessage().contains("更新失败"));
    }

    // 5. 异常场景 - 主键不存在
    @Test
    public void handleRefactor_MissingId() {
        MeetingVo noIdVo = new MeetingVo();
        noIdVo.setName("Meeting without ID");

        // 应该触发异常或错误处理
        assertThrows(NullPointerException.class, () -> {
            meetingService.handleRefactor(noIdVo);
        });
    }

    // ============== clearIsDeleted 方法测试 ==============

    // 1. 正常调用
    @Test
    public void clearIsDeleted_Normal() {
        doNothing().when(meetingMapper).deleteWhereIsDeleted();

        Result result = meetingService.clearIsDeleted();

        assertEquals(Optional.of(200), result.getCode());
        verify(meetingMapper, times(1)).deleteWhereIsDeleted();
    }

    // 2. 异常场景 - 数据库错误
    @Test(expected = RuntimeException.class)
    public void clearIsDeleted_DatabaseError() {
        doThrow(new RuntimeException("DB error")).when(meetingMapper).deleteWhereIsDeleted();

        meetingService.clearIsDeleted();
    }

    // 3. 边界值 - 空表处理
    @Test
    public void clearIsDeleted_EmptyTable() {
        doNothing().when(meetingMapper).deleteWhereIsDeleted();

        Result result = meetingService.clearIsDeleted();
        assertEquals(Optional.of(200), result.getCode());
    }
}