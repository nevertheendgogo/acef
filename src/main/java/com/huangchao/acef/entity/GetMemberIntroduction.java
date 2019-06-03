package com.huangchao.acef.entity;

/**
 * 本类用于中文成员信息接受
 */
public class GetMemberIntroduction {
    private String id;
    private String name;//中文名
    private String position;//中文职位名
    private String description;//中文简介
    private String imgPath;

    @Override
    public String toString() {
        return "GetMemberIntroduction{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", position='" + position + '\'' +
                ", description='" + description + '\'' +
                ", imgPath='" + imgPath + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public GetMemberIntroduction(String id, String name, String position, String description, String imgPath) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.description = description;
        this.imgPath = imgPath;
    }

    public GetMemberIntroduction() {
    }
}
