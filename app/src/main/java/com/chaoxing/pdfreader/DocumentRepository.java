package com.chaoxing.pdfreader;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

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

    public LiveData<DocumentBinding> loadDocument(final String documentPath) {
        Observable.create(new ObservableOnSubscribe<DocumentBinding>() {
            @Override
            public void subscribe(ObservableEmitter<DocumentBinding> emitter) throws Exception {

            }
        }).observeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<DocumentBinding>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(DocumentBinding documentBinding) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
        return result;
    }

}
