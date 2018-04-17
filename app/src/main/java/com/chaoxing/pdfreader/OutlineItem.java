package com.chaoxing.pdfreader;

/**
 * Created by HUWEI on 2018/4/16.
 */

public class OutlineItem {

    private String title;
    private int pageNumber;

    public OutlineItem(String title, int pageNumber) {
        this.title = title;
        this.pageNumber = pageNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

}
