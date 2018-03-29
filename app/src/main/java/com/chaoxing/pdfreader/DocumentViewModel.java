package com.chaoxing.pdfreader;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.chaoxing.pdfreader.util.AbsentLiveData;

/**
 * Created by HUWEI on 2018/3/26.
 */

public class DocumentViewModel extends AndroidViewModel {

    private final MutableLiveData<String> mPath = new MutableLiveData<>();
    private LiveData<Resource<DocumentBinding>> mSimpleDocumentBinding;

    private final MutableLiveData<String> mPassword = new MutableLiveData<>();
    private LiveData<Resource<Boolean>> mCheckPasswordResult;

    private final MutableLiveData<DocumentBinding> mLoadDocument = new MutableLiveData<>();
    private LiveData<Resource<DocumentBinding>> mDocumentBinding;


    public DocumentViewModel(@NonNull Application application) {
        super(application);
        mSimpleDocumentBinding = Transformations.switchMap(mPath, new Function<String, LiveData<Resource<DocumentBinding>>>() {
            @Override
            public LiveData<Resource<DocumentBinding>> apply(String documentPath) {
                return DocumentHandler.get().openDocument(getApplication().getApplicationContext(), documentPath);
            }
        });

        mCheckPasswordResult = Transformations.switchMap(mPassword, new Function<String, LiveData<Resource<Boolean>>>() {
            @Override
            public LiveData<Resource<Boolean>> apply(String password) {
                return DocumentHandler.get().checkPassword(getApplication().getApplicationContext(), mSimpleDocumentBinding.getValue().getData().getDocument(), password);
            }
        });

        mDocumentBinding = Transformations.switchMap(mLoadDocument, new Function<DocumentBinding, LiveData<Resource<DocumentBinding>>>() {
            @Override
            public LiveData<Resource<DocumentBinding>> apply(DocumentBinding document) {
                return DocumentHandler.get().loadDocument(getApplication().getApplicationContext(), document);
            }
        });
    }


    public void openDocument(String path) {
        mPath.setValue(path);
    }

    public LiveData<Resource<DocumentBinding>> getOpenDocumentResult() {
        return mSimpleDocumentBinding;
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

    public LiveData<Resource<DocumentBinding>> getDocumentBinding() {
        return mDocumentBinding;
    }

}
