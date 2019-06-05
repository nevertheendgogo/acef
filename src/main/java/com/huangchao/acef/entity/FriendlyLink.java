package com.huangchao.acef.entity;

/**
 * 本类为友情链接实体类
 */
public class FriendlyLink {
    private String id;
    private String description;
    private String link;
    private String imgPath;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public FriendlyLink() {
    }

    public FriendlyLink(String id, String description, String link, String imgPath) {
        this.id = id;
        this.description = description;
        this.link = link;
        this.imgPath = imgPath;
    }
}
