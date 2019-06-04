package com.huangchao.acef.entity;

/**
 * 本类位普通文章实体类
 */
public class OrdinaryArticle {
    private String articleId;
    private String language;
    private String title;
    private String author;
    private String dislpayTime;//发布时间
    private String content;//文章内容

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
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

    public String getDislpayTime() {
        return dislpayTime;
    }

    public void setDislpayTime(String dislpayTime) {
        this.dislpayTime = dislpayTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public OrdinaryArticle(String articleId, String language, String title, String author, String dislpayTime, String content) {
        this.articleId = articleId;
        this.language = language;
        this.title = title;
        this.author = author;
        this.dislpayTime = dislpayTime;
        this.content = content;
    }

    public OrdinaryArticle() {
    }
}

