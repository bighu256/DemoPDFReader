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

public class PDFDocumentViewModel extends AndroidViewModel {

    private final MutableLiveData<String> mDocumentPath = new MutableLiveData<>();
    private LiveData<DocumentBinding> mDocumentBinding;

    public PDFDocumentViewModel(@NonNull Application application) {
        super(application);
        mDocumentBinding = Transformations.switchMap(mDocumentPath, new Function<String, LiveData<DocumentBinding>>() {
            @Override
            public LiveData<DocumentBinding> apply(String documentPath) {
                if (documentPath == null) {
                    return AbsentLiveData.create();
                } else {
                    return DocumentRepository.create().openDocument(documentPath);
                }
            }
        });
    }

    public LiveData<DocumentBinding> getDocumentBinding() {
        return mDocumentBinding;
    }

    public void openDocument(String path) {
        mDocumentPath.setValue(path);
    }

}
