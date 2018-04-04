package com.chaoxing.pdfreader;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.content.Context;

/**
 * Created by HUWEI on 2018/4/4.
 */

public class PageHandler {

    private final MediatorLiveData<Resource<Integer>> result = new MediatorLiveData<>();

    private int mCurrentPage;
    private int mLoadingPage = -1;

    public LiveData<Resource<Integer>> loadPage(Context context, int page) {
        mCurrentPage = page;
        if(mLoadingPage == -1) {

        }
        return result;
    }

}
