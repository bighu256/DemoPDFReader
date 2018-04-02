package com.chaoxing.pdfreader;

import com.artifex.mupdf.fitz.Document;
import com.artifex.mupdf.fitz.Page;

import java.util.List;

/**
 * Created by HUWEI on 2018/3/26.
 */

public class DocumentBinding {

    private String path;
    private Document document;

    private boolean needsPassword;

    private String title;
    private boolean reflowable;
    private int pageCount;
    private List<Page> pageList;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public boolean isNeedsPassword() {
        return needsPassword;
    }

    public void setNeedsPassword(boolean needsPassword) {
        this.needsPassword = needsPassword;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isReflowable() {
        return reflowable;
    }

    public void setReflowable(boolean reflowable) {
        this.reflowable = reflowable;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public List<Page> getPageList() {
        return pageList;
    }

    public void setPageList(List<Page> pageList) {
        this.pageList = pageList;
    }

}
