package com.neuedu.service;

import com.neuedu.pojo.Course;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.pojo.Meeting;
import com.neuedu.pojo.Result;
import com.neuedu.vo.MeetingVo;
import com.neuedu.vo.PageInfoVo;

/**
 * @author 86138
 * @description 针对表【meeting(会议管理主表)】的数据库操作Service
 * @createDate 2025-06-17 19:44:55
 */
public interface MeetingService extends IService<Meeting> {

    Result getMeetings(PageInfoVo pageInfo);

    /*Result search(String name);*/

    Result sorted(String method);

    Result add(MeetingVo vo);

    Result deleteMeeting(long id);

    Result handleRefactor(MeetingVo vo);

    Result clearIsDeleted();

}