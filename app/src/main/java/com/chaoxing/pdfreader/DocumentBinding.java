package com.chaoxing.pdfreader;

import com.artifex.mupdf.fitz.Document;

/**
 * Created by HUWEI on 2018/3/26.
 */

public class DocumentBinding {

    private Document document;

    private boolean needsPassword;

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
}
