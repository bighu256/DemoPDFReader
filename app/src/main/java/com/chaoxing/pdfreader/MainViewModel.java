package com.chaoxing.pdfreader;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import java.io.File;

/**
 * Created by bighu on 2018/4/3.
 */

public class MainViewModel extends AndroidViewModel {

    private MutableLiveData<File> mDirectory = new MutableLiveData<>();
    private final MediatorLiveData<File[]> mFileList = new MediatorLiveData<>();
    private LiveData<File[]> mFiles;

    public MainViewModel(@NonNull Application application) {
        super(application);

        mFiles = Transformations.switchMap(mDirectory, directory -> {
            if (directory != null && directory.isDirectory()) {
                long length = directory.length();
                File[] f = directory.listFiles();
                mFileList.setValue(directory.listFiles());
            } else {
                mFileList.setValue(new File[]{});
            }
            return mFileList;
        });
    }

    public void openDirectory(File directory) {
        mDirectory.setValue(directory);
    }

    public LiveData<File[]> getFiles() {
        return mFiles;
    }

}
