package com.neuedu.controler;

import com.neuedu.entity.SearchUserRequest;
import com.neuedu.entity.Users;
import com.neuedu.mapper.UsersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class UserController {
    @Autowired
    UsersMapper usersMapper;

    //显示用户信息
    @RequestMapping("/getusers")
    public Map<String, Object> getUsersByPage(@RequestParam(defaultValue = "1") int currentPage,
                                              @RequestParam(defaultValue = "10") int pageSize) {
        int offset = (currentPage - 1) * pageSize;
        List<Users> users = usersMapper.getUsersByPage(offset, pageSize);
        int total = usersMapper.getTotalCount();

        // 格式化日期
        for (Users user : users) {
            Timestamp create_at = user.getCreate_at();
            if (create_at != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                user.setUpdate_create_at(sdf.format(create_at));
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("data", users);
        result.put("total", total);
        result.put("currentPage", currentPage);
        result.put("pageSize", pageSize);

        return result;
    }

    //查询用户信息
    @RequestMapping("/searchusers")
    public Map<String, Object> searchUsers(
            @RequestBody SearchUserRequest request,
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "10") int pageSize) {

        int offset = (currentPage - 1) * pageSize;
        List<Users> users = usersMapper.searchUsersByPage(request, offset, pageSize);
        int total = usersMapper.getSearchCount(request);

        // 格式化日期
        for (Users user : users) {
            Timestamp create_at = user.getCreate_at();
            if (create_at != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                user.setUpdate_create_at(sdf.format(create_at));
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("data", users);
        result.put("total", total);
        result.put("currentPage", currentPage);
        result.put("pageSize", pageSize);

        return result;
    }

    //删除用户信息
    @Transactional
    @RequestMapping("/delusers")
    public int delUsers(int id) {
        usersMapper.delUserInformation(id);
        return usersMapper.delUsers(id);
    }

    //添加用户信息
    @Transactional
    @RequestMapping("/addusers")
    public int addUsers(@RequestBody Users users) {
        Timestamp create_time=users.getCreate_at();
        if (create_time == null) {
            users.setCreate_at(Timestamp.valueOf(LocalDateTime.now()));
        }
        usersMapper.addUserInformation(users);
        return usersMapper.addUsers(users);
    }

    //修改用户信息
    @Transactional
    @RequestMapping("/updateusers")
    public int updateUsers(@RequestBody Users users) {
        usersMapper.updateUsers(users);
        return usersMapper.updateUserInformation(users);
    }

    //通过id查找用户信息
    @RequestMapping("/getuserbyid")
    public Users getUserById(int id){
        return usersMapper.getUser(id);
    }
}
