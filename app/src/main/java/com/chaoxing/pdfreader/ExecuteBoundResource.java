package com.chaoxing.pdfreader;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.content.Context;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

    private Context mApplicationContext;
    private ArgumentType args;
    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();

    private Observable observable = Observable.create(new ObservableOnSubscribe<Resource<ResultType>>() {
        @Override
        public void subscribe(ObservableEmitter<Resource<ResultType>> emitter) throws Exception {
            emitter.onNext(onExecute(mApplicationContext, args));
        }
    }).observeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

    public ExecuteBoundResource(Context context, ArgumentType args) {
        mApplicationContext = context.getApplicationContext();
        this.args = args;
    }

    @MainThread
    private void setValue(Resource<ResultType> newValue) {
        if (!Objects.equals(result.getValue(), newValue)) {
            result.setValue(newValue);
        }
    }

    public final ExecuteBoundResource ready() {
        return ready(null);
    }

    public final ExecuteBoundResource ready(@Nullable Resource<ResultType> data) {
        if (data != null) {
            result.setValue(data);
        } else {
            result.setValue(Resource.loading((ResultType) null));
        }
        return this;
    }

    public final LiveData<Resource<ResultType>> execute() {
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
                setValue(Resource.error(e.getMessage(), null));
            }

            @Override
            public void onComplete() {

            }
        });

        return result;
    }

    public void cancel() {

    }

    @WorkerThread
    protected abstract @NonNull
    Resource<ResultType> onExecute(Context applicationContext, ArgumentType args);

}
