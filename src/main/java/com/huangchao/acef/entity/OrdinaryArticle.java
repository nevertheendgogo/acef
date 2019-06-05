package com.huangchao.acef.entity;

/**
 * 本类位普通文章实体类
 */
public class OrdinaryArticle {
    private String articleId;
    private String part;//所属专题
    private String language;
    private String title;
    private String author;
    private String displayTime;//发布时间
    private String content;//文章内容

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDisplayTime() {
        return displayTime;
    }

    public void setDisplayTime(String displayTime) {
        this.displayTime = displayTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public OrdinaryArticle(String articleId, String part, String language, String title, String author, String displayTime, String content) {
        this.articleId = articleId;
        this.part = part;
        this.language = language;
        this.title = title;
        this.author = author;
        this.displayTime = displayTime;
        this.content = content;
    }

    public OrdinaryArticle() {
    }
}

