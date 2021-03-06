package com.chaoxing.pdfreader;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

/**
 * Created by HUWEI on 2018/3/26.
 */

public class PDFViewModel extends AndroidViewModel {

    private PDFLoader mDocumentHelper = new PDFLoader();
    private PageLoader mPageLoader;

    private final MutableLiveData<String> mPath = new MutableLiveData<>();
    private LiveData<Resource<DocumentBinding>> mOpenDocumentResult;

    private final MutableLiveData<String> mPassword = new MutableLiveData<>();
    private LiveData<Resource<Boolean>> mCheckPasswordResult;

    private final MutableLiveData<DocumentBinding> mLoadDocument = new MutableLiveData<>();
    private LiveData<Resource<DocumentBinding>> mLoadDocumentResult;

    private final MutableLiveData<Integer[]> mLoadPage = new MutableLiveData<>();
    private LiveData<Resource<PageProfile>> mLoadPageResult;

    private final MutableLiveData<DocumentBinding> mLoadOutline = new MutableLiveData<>();
    private LiveData<Resource<DocumentBinding>> mLoadOutlineResult;


    public PDFViewModel(@NonNull Application application) {
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

        mLoadPageResult = Transformations.switchMap(mLoadPage, (args) -> {
            if (mPageLoader == null) {
                mPageLoader = new PageLoaderImpl(getApplication(), getDocumentBinding());
            }
            int pageNumber = args[0];
            int width = args[1];
            return mPageLoader.loadPage(pageNumber, width);
        });

        mLoadOutlineResult = Transformations.switchMap(mLoadOutline, (documentBinding) -> {
            return mDocumentHelper.loadOutline(getApplication().getApplicationContext(), documentBinding);
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

    public void loadPage(int pageNumber, int width) {
        mLoadPage.setValue(new Integer[]{pageNumber, width});
    }

    public LiveData<Resource<PageProfile>> getLoadPageResult() {
        return mLoadPageResult;
    }

    public void loadOutline(DocumentBinding documentBinding) {
        mLoadOutline.setValue(documentBinding);
    }

    public LiveData<Resource<DocumentBinding>> getLoadOutlineResult() {
        return mLoadOutlineResult;
    }

}
