package com.terry.app.front.bean;

/**
 * Created by Taozi on 2016/6/15.
 */
public class MenuItem {
    private String title;
    private int idRes;

    public MenuItem(String title, int idRes) {
        this.title = title;
        this.idRes = idRes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIdRes() {
        return idRes;
    }

    public void setIdRes(int idRes) {
        this.idRes = idRes;
    }
}
