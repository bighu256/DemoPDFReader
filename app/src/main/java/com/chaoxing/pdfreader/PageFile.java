package com.chaoxing.pdfreader;

import com.artifex.mupdf.fitz.Link;
import com.artifex.mupdf.fitz.Rect;

/**
 * Created by bighu on 2018/4/6.
 */

public class PageFile {

    private int pageNumber;
    private String file;
    public Link[] links;
    public Rect[] hits;

    public PageFile() {
    }

    public PageFile(int pageNumber, String file) {
        this.pageNumber = pageNumber;
        this.file = file;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Link[] getLinks() {
        return links;
    }

    public void setLinks(Link[] links) {
        this.links = links;
    }

    public Rect[] getHits() {
        return hits;
    }

    public void setHits(Rect[] hits) {
        this.hits = hits;
    }
}
