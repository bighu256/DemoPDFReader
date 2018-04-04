package com.chaoxing.pdfreader;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

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
            File[] files = null;
            if (directory != null && directory.isDirectory()) {
                files = directory.listFiles();
            }

            if (files == null) {
                files = new File[]{};
            }

            Arrays.sort(files, (f1, f2) -> compare(f1, f2));
            mFileList.setValue(files);
            return mFileList;
        });
    }

    public void openDirectory(File directory) {
        mDirectory.setValue(directory);
    }

    public LiveData<File[]> getFiles() {
        return mFiles;
    }

    public int compare(File f1, File f2) {
        return f1.getName().compareToIgnoreCase(f2.getName());
    }
}
