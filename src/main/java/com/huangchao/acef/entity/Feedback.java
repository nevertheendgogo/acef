package com.huangchao.acef.entity;

/**
 * 此类位意见反馈实体类
 */
public class Feedback {

    private int id;
    private String emailAccount;//邮箱
    private String userName;
    private String phone;
    private String title;
    private String description;
    private String createTime;

    @Override
    public String toString() {
        return "Feedback{" +
                "id=" + id +
                ", emailAccount='" + emailAccount + '\'' +
                ", userName='" + userName + '\'' +
                ", phone='" + phone + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", createTime='" + createTime + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmailAccount() {
        return emailAccount;
    }

    public void setEmailAccount(String emailAccount) {
        this.emailAccount = emailAccount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Feedback(int id, String emailAccount, String userName, String phone, String title, String description, String createTime) {
        this.id = id;
        this.emailAccount = emailAccount;
        this.userName = userName;
        this.phone = phone;
        this.title = title;
        this.description = description;
        this.createTime = createTime;
    }

    public Feedback() {
    }
}
