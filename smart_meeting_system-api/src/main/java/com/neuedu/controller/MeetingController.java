package com.neuedu.controller;

import com.neuedu.pojo.Result;
import com.neuedu.service.MeetingService;
import com.neuedu.vo.MeetingVo;
import com.neuedu.vo.PageInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("meeting")
@CrossOrigin
public class MeetingController {
    @Autowired
    MeetingService meetingService;

    @PostMapping("getMeetings")
    public Result getMeetings(@RequestBody PageInfoVo pageInfo){
        System.out.println(pageInfo);
        Result result = meetingService.getMeetings(pageInfo);
        return result;
    }

    @GetMapping("getMeetingById")
    public Result getMeetingById(@RequestParam long id){

        return meetingService.getMeetingById(id);
    }

    @GetMapping("sorted")
    public Result sorted(String method){
        Result result = meetingService.sorted(method);
        return result;
    }

    @PostMapping("handleAdd")
    private Result add(@RequestBody MeetingVo vo){

        Result result = meetingService.add(vo);
        System.out.println(vo);
        return result;
    }

    @GetMapping("deleteMeeting")
    private Result deleteMeeting(long id){
        Result result = meetingService.deleteMeeting(id);
        return result;
    }

    @PutMapping("handleRefactor")
    private Result handleRefactor(@RequestBody MeetingVo vo){
        System.out.println(vo);
        Result result = meetingService.handleRefactor(vo);
        return result;
    }

    @GetMapping("clearIsDeleted")
    private  Result clearIsDeleted(){
        Result result = meetingService.clearIsDeleted();
        return result;
    }

}