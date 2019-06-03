package com.huangchao.acef.entity;

public class Slideshow {
    private int id;
    private String url;

    @Override
    public String toString() {
        return "Slideshow{" +
                "id=" + id +
                ", url='" + url + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Slideshow(int id, String url) {
        this.id = id;
        this.url = url;
    }

    public Slideshow() {
    }
}
