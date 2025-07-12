package com.neuedu.pojo;


<<<<<<< HEAD
public class Enterprise {
    private Long id;

    private String enterpriseMark;
    private String name;
    private String contactPerson;
    private String phone;
    private String managerUsername;

    private String enterpriseIcon;
=======
import com.fasterxml.jackson.annotation.JsonProperty;

public class Enterprise {
    private Long id;
    private String enterprise_mark;
    private String name;
    private String contact_person;
    private String phone;
    private String manager_username;

    private String enterprise_icon;
>>>>>>> eabf3a5c3f406a5439a9b3dc854c3d2a9a6c92d5
    private String comment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEnterprise_mark() {
<<<<<<< HEAD
        return enterpriseMark;
    }

    public void setEnterprise_mark(String enterpriseMark) {
        this.enterpriseMark = enterpriseMark;
=======
        return enterprise_mark;
    }

    public void setEnterprise_mark(String enterprise_mark) {
        this.enterprise_mark = enterprise_mark;
>>>>>>> eabf3a5c3f406a5439a9b3dc854c3d2a9a6c92d5
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact_person() {
<<<<<<< HEAD
        return contactPerson;
    }

    public void setContact_person(String contactPerson) {
        this.contactPerson = contactPerson;
=======
        return contact_person;
    }

    public void setContact_person(String contact_person) {
        this.contact_person = contact_person;
>>>>>>> eabf3a5c3f406a5439a9b3dc854c3d2a9a6c92d5
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getManager_username() {
<<<<<<< HEAD
        return managerUsername;
    }

    public void setManager_username(String managerUsername) {
        this.managerUsername = managerUsername;
    }

    public String  getEnterprise_icon() {
        return enterpriseIcon;
    }

    public void setEnterprise_icon(String  enterpriseIcon) {
        this.enterpriseIcon = enterpriseIcon;
=======
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
>>>>>>> eabf3a5c3f406a5439a9b3dc854c3d2a9a6c92d5
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> eabf3a5c3f406a5439a9b3dc854c3d2a9a6c92d5
