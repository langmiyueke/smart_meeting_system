package com.neuedu.pojo;


import com.fasterxml.jackson.annotation.JsonProperty;

public class Enterprise {
    private Long id;
    private String enterprise_mark;
    private String name;
    private String contact_person;
    private String phone;
    private String manager_username;

    private String enterprise_icon;
    private String comment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEnterprise_mark() {
        return enterprise_mark;
    }

    public void setEnterprise_mark(String enterprise_mark) {
        this.enterprise_mark = enterprise_mark;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact_person() {
        return contact_person;
    }

    public void setContact_person(String contact_person) {
        this.contact_person = contact_person;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getManager_username() {
        return manager_username;
    }

    public void setManager_username(String manager_username) {
        this.manager_username = manager_username;
    }

    public String  getEnterprise_icon() {
        return enterprise_icon;
    }

    public void setEnterprise_icon(String  enterprise_icon) {
        this.enterprise_icon = enterprise_icon;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}