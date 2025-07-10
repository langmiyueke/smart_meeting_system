package com.neuedu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neuedu.pojo.Meeting;
import com.neuedu.pojo.Result;
import com.neuedu.service.MeetingService;
import com.neuedu.mapper.MeetingMapper;
import com.neuedu.vo.MeetingVo;
import com.neuedu.vo.PageInfoVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLOutput;
import java.util.*;

/**
 * @author 86138
 * @description 针对表【meeting(课程管理主表)】的数据库操作Service实现
 * @createDate 2025-06-17 19:44:55
 */

@Service
public class MeetingServiceImpl extends ServiceImpl<MeetingMapper, Meeting>
        implements MeetingService{

    @Autowired
    MeetingMapper meetingMapper;

    @Override
    public Result getMeetings(PageInfoVo info) {
        IPage<Map> page = new Page<>(info.getPageNum(), info.getPageSize());

        meetingMapper.selectMyPage(page,info.getKeyWords(),info.getSortBy());

        Map<String,Object> pageInfo = new HashMap<>();
        pageInfo.put("pageData",page.getRecords());

        pageInfo.put("pageNum",page.getCurrent());
        pageInfo.put("pageSize",page.getSize());
        pageInfo.put("totalSize", page.getTotal());
        Map<String,Object> data = new HashMap<>();
        data.put("pageInfo",pageInfo);
        return Result.success(data);
    }

   /* @Override
    public Result search(String name) {
        List<Map<String, Object>> meetings = meetingMapper.selectByMeetingName(name);
        MeetingVo[] data = new MeetingVo[meetings.size()];
        Arrays.setAll(data, i -> new MeetingVo());

        for (int i = 0; i < meetings.size(); i++) {
            Map<String, Object> map = meetings.get(i);
            MeetingVo meetingVo = data[i];

            // 这里字段名要和你SQL语句中SELECT的列名一致
            Object idObj = map.get("id");
            Object nameObj = map.get("meeting_name");
            Object authorObj = map.get("author");
            Object descObj = map.get("description");

            if (idObj != null) {
                meetingVo.setId(((Number) idObj).intValue());
            }
            if (nameObj != null) {
                meetingVo.setName(nameObj.toString());
            }
            if (authorObj != null) {
                meetingVo.setAuthor(authorObj.toString());
            }
            if (descObj != null) {
                meetingVo.setDescription(descObj.toString());
            }
        }
        return Result.success(data);
    }*/

    @Override
    public Result sorted(String method) {
        List<Meeting> meetings = meetingMapper.selectList(null);
        MeetingVo[] data = new MeetingVo[meetings.size()];
        Arrays.setAll(data,i->new MeetingVo());
        for (int i = 0; i < meetings.size(); i++) {
            Meeting meeting = meetings.get(i);
            MeetingVo m = data[i];
            m.setId(Math.toIntExact(meeting.getId()));
            m.setCreator(meeting.getCreator());
            m.setName(meeting.getName());
            m.setContent(meeting.getContent());
            m.setIsEffective(meeting.getIsEffective());

        }
        Arrays.sort(data, new Comparator<MeetingVo>() {
            @Override
            public int compare(MeetingVo o1, MeetingVo o2) {
                if (method.equals("creator")) {
                    return o1.getCreator().compareTo(o2.getCreator()); // 按照会议创建者creator排序
                } else {
                    return o1.getName().compareTo(o2.getName());    // 按 name 字符串排序
                }
            }
        });

        return Result.success(data);
    }

    @Override
    public Result add(MeetingVo vo) {
        Meeting meeting = new Meeting();
        BeanUtils.copyProperties(vo, meeting); // 只复制同名字段
        meeting.setName(vo.getName()); // 单独处理字段名不一样的
        meeting.setIsDeleted(0);

        //初始时会议状态设置为无效 == 0
        meeting.setIsEffective(0);
        String content = meeting.getContent();
        if (content.startsWith("<p>") && content.endsWith("</p>")) {
            content = content.substring(3, content.length() - 4);
        }
        meeting.setContent(content);
        meetingMapper.insert(meeting);
        return Result.success();
    }

    @Override
    public Result deleteMeeting(long id) {
        System.out.println(id);
        Meeting meeting = meetingMapper.selectById(id);
        if(meeting==null){
            return Result.error("课程不存在");
        }
        System.out.println("查询到的课程: " + meeting);
        int i = meetingMapper.deleteById(id);
        System.out.println("影响行数: " + i);
        if(i>0) return Result.success();
        return Result.error("删除失败");
    }

    @Override
    public Result handleRefactor(MeetingVo vo) {
        System.out.println("前端传来 ID：" + vo.getId());

        Meeting meeting = new Meeting();
        BeanUtils.copyProperties(vo, meeting);
        meeting.setName(vo.getName());
        meeting.setId((int) vo.getId());
        String content = meeting.getContent();
        if (content.startsWith("<p>") && content.endsWith("</p>")) {
            content = content.substring(3, content.length() - 4);
            
        }
        meeting.setContent(content);
        int rows = meetingMapper.updateById(meeting);
        if (rows == 0) {
            return Result.error("更新失败，课程可能不存在");
        }

        return Result.success();
    }

    @Override
    public Result clearIsDeleted() {
        meetingMapper.deleteWhereIsDeleted();
        return Result.success();
    }

    @Override
    public Result getMeetingById(long id) {
        Meeting meeting = meetingMapper.selectById(id);
        if(meeting==null){
            return Result.error("课程不存在");
        }
        System.out.println("查询到的课程: " + meeting);
        return Result.success(meeting);
    }

    @Override
    public Result reViewMeeting(long id) {
        System.out.println(id);
        Meeting meeting = meetingMapper.selectById(id);
        String message = "";

        if(meeting==null){
            return Result.error("会议不存在");
        }
        System.out.println("查询到的会议: " + meeting);
        if(meeting.getIsEffective() == null) {

            System.out.println("该会议状态异常，审核通过失败");
            message = "该会议状态异常，审核通过失败";

        } else if(meeting.getIsEffective() == 1) {
            System.out.println("该会议状态为有效，无需再次审核通过");
            message = "该会议状态为有效，无需再次审核通过";
        } else {
            meeting.setIsEffective(1);
            System.out.println("会议审核通过");
            message = "会议审核通过";
        }

        int i = meetingMapper.updateById(meeting);
        System.out.println("影响行数: " + i);
        if(i>0) return Result.success(message);

        return Result.error(message);
    }


}




