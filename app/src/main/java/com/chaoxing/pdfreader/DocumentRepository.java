package com.chaoxing.pdfreader;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import com.artifex.mupdf.fitz.Document;
import com.chaoxing.pdfreader.util.Utils;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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

    public LiveData<Resource<DocumentBinding>> openDocument(String path) {
        return new ExecuteBoundResource<String, DocumentBinding>(path) {
            @Override
            public Resource<DocumentBinding> onExecute(String args) {
                if (Utils.isBlank(args)) {
                    return Resource.error("文档打开失败", null);
                }

                DocumentBinding binding = new DocumentBinding();
                Document document = Document.openDocument(args);
                binding.setDocument(document);
                binding.setNeedsPassword(document.needsPassword());

                return Resource.success(binding);
            }
        }.asLiveData();
    }

}
