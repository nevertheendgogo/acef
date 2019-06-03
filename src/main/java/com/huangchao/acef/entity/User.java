package com.huangchao.acef.entity;

/**
 * 本类为用户实体类
 */
public class User {
    private String id;
    private String emailAccount;    //账号（邮箱）
    private String password;


    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", emailAccount='" + emailAccount + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public User(String id, String emailAccount, String password) {
        this.id = id;
        this.emailAccount = emailAccount;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmailAccount() {
        return emailAccount;
    }

    public void setEmailAccount(String emailAccount) {
        this.emailAccount = emailAccount;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User() {
    }
}
