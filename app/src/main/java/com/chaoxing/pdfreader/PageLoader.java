package com.chaoxing.pdfreader;

import android.arch.lifecycle.LiveData;

/**
 * Created by HUWEI on 2018/4/8.
 */

public interface PageLoader {

    LiveData<Resource<PageProfile>> loadPage(int pageNumber, int width);

}
