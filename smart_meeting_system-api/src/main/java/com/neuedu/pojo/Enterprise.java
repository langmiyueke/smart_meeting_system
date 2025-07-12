package com.neuedu.pojo;


public class Enterprise {
    private Long id;

    private String enterpriseMark;
    private String name;
    private String contactPerson;
    private String phone;
    private String managerUsername;

    private String enterpriseIcon;
    private String comment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEnterprise_mark() {
        return enterpriseMark;
    }

    public void setEnterprise_mark(String enterpriseMark) {
        this.enterpriseMark = enterpriseMark;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact_person() {
        return contactPerson;
    }

    public void setContact_person(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getManager_username() {
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
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
