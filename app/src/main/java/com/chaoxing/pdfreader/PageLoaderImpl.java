package com.chaoxing.pdfreader;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.content.Context;
import android.graphics.Bitmap;

import com.artifex.mupdf.fitz.Link;
import com.artifex.mupdf.fitz.Matrix;
import com.artifex.mupdf.fitz.Page;
import com.artifex.mupdf.fitz.android.AndroidDrawDevice;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by HUWEI on 2018/4/8.
 */

public class PageLoaderImpl implements PageLoader {

    private final MediatorLiveData<Resource<PageProfile>> result = new MediatorLiveData<>();

    private Context mContext;
    private DocumentBinding mDocumentBinding;
    private File mCacheDirectory;

    private Set<String> mLoading = new HashSet<>();

    public PageLoaderImpl(Context context, DocumentBinding documentBinding) {
        mContext = context;
        mDocumentBinding = documentBinding;
        mCacheDirectory = new File(context.getExternalCacheDir(), "pdf");
        if (!mCacheDirectory.exists()) {
            mCacheDirectory.mkdirs();
        }
    }

    @Override
    public synchronized LiveData<Resource<PageProfile>> loadPage(int pageNumber, int width) {
        String key = String.format("%04x%4d", pageNumber, width);
        if (mLoading.contains(key)) {
            result.setValue(Resource.loading(new PageProfile(pageNumber)));
        } else {

            mLoading.add(key);

            Observable.create((ObservableEmitter<Resource<PageProfile>> emitter) -> {
                emitter.onNext(Resource.loading(new PageProfile(pageNumber)));
                emitter.onNext(drawPage(pageNumber, width));
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Resource<PageProfile>>() {
                        Disposable d;

                        @Override
                        public void onSubscribe(Disposable d) {
                            this.d = d;
                        }

                        @Override
                        public void onNext(Resource<PageProfile> pageResource) {
                            if (!pageResource.isLoading()) {
                                mLoading.remove(key);
                            }
                            result.setValue(pageResource);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mLoading.remove(key);
                            result.setValue(Resource.error(e.getMessage(), new PageProfile(pageNumber)));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });

        }
        return result;
    }


    private Resource<PageProfile> drawPage(int pageNumber, int fitWidth) {
        Resource<PageProfile> pageResource = null;
        PageProfile profile = new PageProfile(pageNumber);
        try {
            Page page = mDocumentBinding.getDocument().loadPage(pageNumber);
            Matrix matrix = AndroidDrawDevice.fitPageWidth(page, fitWidth);

            File pageFile = null;
            File file = getPageFile(pageNumber, fitWidth);
            if (file.exists() && file.isFile()) {
                pageFile = file;
            } else {
                Bitmap bitmap = AndroidDrawDevice.drawPage(page, matrix);
                if (bitmap != null) {
                    FileOutputStream fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.WEBP, 80, fos);
                    fos.close();
                    pageFile = file;
                } else {
                    pageResource = Resource.error("页面绘制失败", profile);
                }
            }

            if (pageFile != null) {
                profile.setPageFile(pageFile.getAbsolutePath());
                Link[] links = page.getLinks();
                if (links != null) {
                    for (Link link : links) {
                        link.bounds.transform(matrix);
                    }
                }
                profile.setLinks(links);
                page.destroy();
                pageResource = Resource.success(profile);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            pageResource = Resource.error(t.getMessage(), profile);
        }
        return pageResource;
    }

    private File getPageFile(int pageNumber, int width) {
        return new File(mCacheDirectory, mDocumentBinding.getMd5() + String.format("%04x%4d", pageNumber, width) + ".tmp");
    }

}
