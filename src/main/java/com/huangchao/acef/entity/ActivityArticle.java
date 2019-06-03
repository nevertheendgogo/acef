package com.huangchao.acef.entity;

import java.util.List;

public class ActivityArticle {
    private String articleId;
    private String language;
    private String title;
    private String author;
    private String displayTime;//发布时间：年月日
    private String activityStartTime;//活动开始时间：年月日
    private String activityEndTime;//活动结束时间：年月日
    private String content;//文章内容
    private String entryFormUrl;//报名表
    private String posterUrl;//海报

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

    public String getDisplayTime() {
        return displayTime;
    }

    public void setDisplayTime(String displayTime) {
        this.displayTime = displayTime;
    }

    public String getActivityStartTime() {
        return activityStartTime;
    }

    public void setActivityStartTime(String activityStartTime) {
        this.activityStartTime = activityStartTime;
    }

    public String getActivityEndTime() {
        return activityEndTime;
    }

    public void setActivityEndTime(String activityEndTime) {
        this.activityEndTime = activityEndTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEntryFormUrl() {
        return entryFormUrl;
    }

    public void setEntryFormUrl(String entryFormUrl) {
        this.entryFormUrl = entryFormUrl;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public ActivityArticle(String articleId, String language, String title, String author, String displayTime, String activityStartTime, String activityEndTime, String content, String entryFormUrl, String posterUrl) {
        this.articleId = articleId;
        this.language = language;
        this.title = title;
        this.author = author;
        this.displayTime = displayTime;
        this.activityStartTime = activityStartTime;
        this.activityEndTime = activityEndTime;
        this.content = content;
        this.entryFormUrl = entryFormUrl;
        this.posterUrl = posterUrl;
    }

    public ActivityArticle() {
    }
}
