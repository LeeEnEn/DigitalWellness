package com.example.digitalwellness;

public class MyModel {


    String title, data, progress, pageNum;
    int image;

    public MyModel(String title, String data, String progress, String pageNum, int image) {
        this.title = title;
        this.data = data;
        this.progress = progress;
        this.pageNum = pageNum;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getPageNum() {
        return pageNum;
    }

    public void setPageNum(String pageNum) {
        this.pageNum = pageNum;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}