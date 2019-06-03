package com.huangchao.acef.entity;

/**
 * 本类用于中文成员信息接受
 */
public class MemberIntroduction {
    private String id;
    private String chName;//中文名
    private String chPos;//中文职位名
    private String chDes;//中文简介
    private String frName;//法文名
    private String frPos;//法文职位名
    private String frDes;//法文简介
    private int showPriority;//展示优先级
    private String imgPath;

    @Override
    public String toString() {
        return "MemberIntroduction{" +
                "id='" + id + '\'' +
                ", chName='" + chName + '\'' +
                ", chPos='" + chPos + '\'' +
                ", chDes='" + chDes + '\'' +
                ", frName='" + frName + '\'' +
                ", frPos='" + frPos + '\'' +
                ", frDes='" + frDes + '\'' +
                ", showPriority=" + showPriority +
                ", imgPath='" + imgPath + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChName() {
        return chName;
    }

    public void setChName(String chName) {
        this.chName = chName;
    }

    public String getChPos() {
        return chPos;
    }

    public void setChPos(String chPos) {
        this.chPos = chPos;
    }

    public String getChDes() {
        return chDes;
    }

    public void setChDes(String chDes) {
        this.chDes = chDes;
    }

    public String getFrName() {
        return frName;
    }

    public void setFrName(String frName) {
        this.frName = frName;
    }

    public String getFrPos() {
        return frPos;
    }

    public void setFrPos(String frPos) {
        this.frPos = frPos;
    }

    public String getFrDes() {
        return frDes;
    }

    public void setFrDes(String frDes) {
        this.frDes = frDes;
    }

    public int getShowPriority() {
        return showPriority;
    }

    public void setShowPriority(int showPriority) {
        this.showPriority = showPriority;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public MemberIntroduction(String id, String chName, String chPos, String chDes, String frName, String frPos, String frDes, int showPriority, String imgPath) {
        this.id = id;
        this.chName = chName;
        this.chPos = chPos;
        this.chDes = chDes;
        this.frName = frName;
        this.frPos = frPos;
        this.frDes = frDes;
        this.showPriority = showPriority;
        this.imgPath = imgPath;
    }

    public MemberIntroduction() {
    }
}
