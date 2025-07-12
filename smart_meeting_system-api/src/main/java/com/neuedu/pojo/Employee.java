package com.neuedu.pojo;

import java.sql.Timestamp;

public class Employee {
    private int id;

    private String username;
    private String password;
    private String role;
<<<<<<< HEAD
    private String enterpriseName;
=======
    private String enterprise_name;
>>>>>>> eabf3a5c3f406a5439a9b3dc854c3d2a9a6c92d5

    private String nickname;
    private String phone;
    private String email;
    private String sex;
    private String state;
    private String department;
    private String job;
    private String comment;
<<<<<<< HEAD
    private Timestamp createAt;

    private String updateCreateAt;
=======
    private Timestamp create_at;

    private String update_create_at;
>>>>>>> eabf3a5c3f406a5439a9b3dc854c3d2a9a6c92d5

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Timestamp getCreate_at() {
<<<<<<< HEAD
        return createAt;
    }

    public void setCreate_at(Timestamp createAt) {
        this.createAt = createAt;
    }

    public String getEnterprise_name() {
        return enterpriseName;
    }

    public void setEnterprise_name(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public String getUpdate_create_at() {
        return updateCreateAt;
    }

    public void setUpdate_create_at(String update_creat_at) {
        this.updateCreateAt = updateCreateAt;
    }
}
=======
        return create_at;
    }

    public void setCreate_at(Timestamp create_at) {
        this.create_at = create_at;
    }

    public String getEnterprise_name() {
        return enterprise_name;
    }

    public void setEnterprise_name(String enterprise_name) {
        this.enterprise_name = enterprise_name;
    }


    public String getUpdate_create_at() {
        return update_create_at;
    }

    public void setUpdate_create_at(String update_creat_at) {
        this.update_create_at = update_creat_at;
    }
}
>>>>>>> eabf3a5c3f406a5439a9b3dc854c3d2a9a6c92d5
