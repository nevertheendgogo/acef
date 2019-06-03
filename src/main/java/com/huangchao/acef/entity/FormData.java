package com.huangchao.acef.entity;

/**
 * 此类为表单数据实体类
 */
public class FormData {

    //数据位置
    private String id;
    private String author;
    //标题
    private String time="2019-05-30 08:08:08";
    private String title;
    //描述
    private String description;
    //图片地址
    private String imePath;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public String getImePath() {
        return imePath;
    }

    public void setImePath(String imePath) {
        this.imePath = imePath;
    }

    public FormData(String id, String author, String time, String title, String description, String imePath) {
        this.id = id;
        this.author = author;
        this.time = time;
        this.title = title;
        this.description = description;
        this.imePath = imePath;
    }

    public FormData() {
    }
}
