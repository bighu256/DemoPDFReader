package com.chaoxing.pdfreader;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

/**
 * Created by HUWEI on 2018/3/26.
 */

public class DocumentRepository {


    private final MediatorLiveData<DocumentBinding> result = new MediatorLiveData<>();

    private DocumentRepository() {
    }

    public static DocumentRepository create() {
        return new DocumentRepository();
    }

    public LiveData<DocumentBinding> loadDocument(String documentPath) {
        
        return result;
    }

}
