package com.chaoxing.pdfreader;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.artifex.mupdf.fitz.Document;

/**
 * Created by HUWEI on 2018/3/26.
 */

public class PDFDocumentActivity extends AppCompatActivity {

    private PDFDocumentViewModel mViewModel;

    protected Document mDocument;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(PDFDocumentViewModel.class);

        Uri uri = getIntent().getData();
        String mimetype = getIntent().getType();
        String uriStr = uri.toString();

        final String path = UriUtils.getRealPath(this, uri);

        if (path == null) {
            finish();
            return;
        }

        mViewModel.getDocumentBinding().observe(this, new Observer<DocumentBinding>() {
            @Override
            public void onChanged(@Nullable DocumentBinding documentBinding) {
                if (documentBinding != null) {

                }
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mViewModel.openDocument(path);
            }
        }, 1000);


    }

}
