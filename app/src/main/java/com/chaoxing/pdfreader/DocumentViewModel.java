package com.chaoxing.pdfreader;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.artifex.mupdf.fitz.Page;

import java.util.List;

/**
 * Created by HUWEI on 2018/3/26.
 */

public class DocumentViewModel extends AndroidViewModel {

    private DocumentHelper mDocumentHelper = new DocumentHelper();

    private final MutableLiveData<String> mPath = new MutableLiveData<>();
    private LiveData<Resource<DocumentBinding>> mOpenDocumentResult;

    private final MutableLiveData<String> mPassword = new MutableLiveData<>();
    private LiveData<Resource<Boolean>> mCheckPasswordResult;

    private final MutableLiveData<DocumentBinding> mLoadDocument = new MutableLiveData<>();
    private LiveData<Resource<DocumentBinding>> mLoadDocumentResult;

    private final MutableLiveData<Integer> mLoadPage = new MutableLiveData<>();
    private LiveData<Resource<Integer>> mLoadPageResult;


    public DocumentViewModel(@NonNull Application application) {
        super(application);
        mOpenDocumentResult = Transformations.switchMap(mPath, documentPath -> {
            return mDocumentHelper.openDocument(getApplication().getApplicationContext(), documentPath);
        });

        mCheckPasswordResult = Transformations.switchMap(mPassword, password -> {
            return mDocumentHelper.checkPassword(getApplication().getApplicationContext(), mOpenDocumentResult.getValue().getData().getDocument(), password);
        });

        mLoadDocumentResult = Transformations.switchMap(mLoadDocument, documentBinding -> {
            return mDocumentHelper.loadDocument(getApplication().getApplicationContext(), documentBinding);
        });

        mLoadPageResult = Transformations.switchMap(mLoadPage, documentBinding -> {
            return null;
        });
    }


    public void openDocument(String path) {
        mPath.setValue(path);
    }

    public LiveData<Resource<DocumentBinding>> getOpenDocumentResult() {
        return mOpenDocumentResult;
    }

    public void checkPassword(final String password) {
        mPassword.setValue(password);
    }

    public LiveData<Resource<Boolean>> getCheckPasswordResult() {
        return mCheckPasswordResult;
    }

    public void loadDocument() {
        mLoadDocument.setValue(getOpenDocumentResult().getValue().getData());
    }

    public LiveData<Resource<DocumentBinding>> getLoadDocumentResult() {
        return mLoadDocumentResult;
    }

    public DocumentBinding getDocumentBinding() {
        return mLoadDocumentResult.getValue().getData();
    }

    public List<Integer> getLoadPageResult() {
//        return mLoadPageListResult.getValue().getData();
        return null;
    }

}
