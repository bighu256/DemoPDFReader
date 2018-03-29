package com.chaoxing.pdfreader;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.artifex.mupdf.fitz.Document;

/**
 * Created by HUWEI on 2018/3/26.
 */

public class PDFDocumentActivity extends AppCompatActivity {

    private DocumentViewModel mViewModel;

    protected Document mDocument;

    private AlertDialog mInputPasswordDialog;
    private ProgressBar mPbLoading;
    private TextView mTvMessage;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_document);
        mPbLoading = findViewById(R.id.pb_loading);
        mTvMessage = findViewById(R.id.tv_message);

        mViewModel = ViewModelProviders.of(this).get(DocumentViewModel.class);

        Uri uri = getIntent().getData();
        String mimetype = getIntent().getType();
        String uriStr = uri.toString();

        final String path = UriUtils.getRealPath(this, uri);

        if (path == null) {
            finish();
            return;
        }

        initDocument();

        mViewModel.openDocument(path);

    }

    private void initDocument() {
        mViewModel.getOpenDocumentResult().observe(this, mObserverOpenDocument);
        mViewModel.getCheckPasswordResult().observe(PDFDocumentActivity.this, mObserverCheckPassword);
        mViewModel.getDocumentBinding().observe(this, mObserverLoadDocument);
    }

    private Observer<Resource<DocumentBinding>> mObserverOpenDocument = new Observer<Resource<DocumentBinding>>() {
        @Override
        public void onChanged(@Nullable Resource<DocumentBinding> documentBinding) {
            Status status = documentBinding.getStatus();
            if (status == Status.LOADING) {
                mTvMessage.setVisibility(View.GONE);
                mPbLoading.setVisibility(View.VISIBLE);
            } else if (status == Status.ERROR) {
                mPbLoading.setVisibility(View.GONE);
                mTvMessage.setText(documentBinding.getMessage());
                mTvMessage.setVisibility(View.VISIBLE);
            } else if (status == Status.SUCCESS) {
                if (documentBinding.getData().isNeedsPassword()) {
                    mPbLoading.setVisibility(View.GONE);
                    askPassword();
                } else {
                    loadDocument();
                }
            }
        }
    };

    private void askPassword() {
        if (mInputPasswordDialog == null) {
            final AppCompatEditText etPassword = new AppCompatEditText(this);
            etPassword.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

            mInputPasswordDialog = new AlertDialog.Builder(this)
                    .setTitle("输入密码")
                    .setView(etPassword)
                    .setPositiveButton("确定", null)
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .create();
            mInputPasswordDialog.show();
            mInputPasswordDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (etPassword.length() > 0) {
                        mInputPasswordDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        mInputPasswordDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(false);
                        mViewModel.checkPassword(etPassword.getText().toString());
                    }
                }
            });
        } else {
            mInputPasswordDialog.show();
        }
    }

    Observer<Resource<Boolean>> mObserverCheckPassword = new Observer<Resource<Boolean>>() {
        @Override
        public void onChanged(@Nullable Resource<Boolean> result) {
            mInputPasswordDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
            mInputPasswordDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(true);
            if (result.getData()) {
                mInputPasswordDialog.dismiss();
                loadDocument();
            } else {
                Toast.makeText(PDFDocumentActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void loadDocument() {
        mViewModel.loadDocument();
    }

    Observer<Resource<DocumentBinding>> mObserverLoadDocument = new Observer<Resource<DocumentBinding>>() {
        @Override
        public void onChanged(@Nullable Resource<DocumentBinding> documentBinding) {
            if (documentBinding.isLoading()) {
                mPbLoading.setVisibility(View.VISIBLE);
            } else if (documentBinding.isSuccessful()) {
                mPbLoading.setVisibility(View.GONE);
            } else {
                mPbLoading.setVisibility(View.VISIBLE);
                Toast.makeText(PDFDocumentActivity.this, documentBinding.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    };

}
