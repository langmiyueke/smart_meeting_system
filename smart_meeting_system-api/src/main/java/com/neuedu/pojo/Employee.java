package com.neuedu.pojo;

import java.sql.Timestamp;

public class Employee {
    private int id;

    private String username;
    private String password;
    private String role;
    private String enterpriseName;

    private String nickname;
    private String phone;
    private String email;
    private String sex;
    private String state;
    private String department;
    private String job;
    private String comment;
    private Timestamp createAt;

    private String updateCreateAt;

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
