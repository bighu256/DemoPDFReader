package com.chaoxing.pdfreader;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;

import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by bighu on 2018/3/28.
 */

public abstract class ExecuteBoundResource<ArgumentType, ResultType> {

    private ArgumentType args;
    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();
    private Observable observable = Observable.create(new ObservableOnSubscribe<Resource<ResultType>>() {
        @Override
        public void subscribe(ObservableEmitter<Resource<ResultType>> emitter) throws Exception {
            Resource result = onExecute(args);
            if (result == null) {
                result = Resource.error(null, null);
            }
            emitter.onNext(result);
        }
    }).observeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

    public ExecuteBoundResource(ArgumentType args) {
        this.args = args;
        result.setValue(Resource.loading((ResultType) null));
    }

    @MainThread
    private void setValue(Resource<ResultType> newValue) {
        if (!Objects.equals(result.getValue(), newValue)) {
            result.setValue(newValue);
        }
    }

    public LiveData<Resource<ResultType>> asLiveData() {
        return result;
    }

    public void execute() {
        observable.subscribe(new Observer<Resource<ResultType>>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Resource<ResultType> result) {
                setValue(result);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void cancel() {

    }

    @WorkerThread
    public abstract Resource<ResultType> onExecute(ArgumentType args);

}
