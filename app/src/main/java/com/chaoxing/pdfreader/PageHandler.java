package com.chaoxing.pdfreader;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;

import com.artifex.mupdf.fitz.Cookie;
import com.artifex.mupdf.fitz.Link;
import com.artifex.mupdf.fitz.Matrix;
import com.artifex.mupdf.fitz.Page;
import com.artifex.mupdf.fitz.Rect;
import com.artifex.mupdf.fitz.RectI;
import com.artifex.mupdf.fitz.android.AndroidDrawDevice;

import java.io.File;
import java.io.FileOutputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by HUWEI on 2018/4/4.
 */

public class PageHandler {

    private final MediatorLiveData<Resource<PageFile>> result = new MediatorLiveData<>();

    private Context mContext;
    private DocumentBinding mDocumentBinding;
    private File mCacheDirectory;

    private int mCurrentPage;
    private int mLoadingPage = -1;

    public PageHandler(Context context, DocumentBinding documentBinding) {
        mContext = context.getApplicationContext();
        mCacheDirectory = new File(context.getExternalCacheDir(), "pdf");
        if (!mCacheDirectory.exists()) {
            mCacheDirectory.mkdirs();
        }
        mDocumentBinding = documentBinding;
    }

    public LiveData<Resource<PageFile>> loadPage(final int pageNumber) {
        mCurrentPage = pageNumber;
        if (mLoadingPage != -1) {
            return result;
        }
        mLoadingPage = pageNumber;
        Observable.create(new ObservableOnSubscribe<Resource<PageFile>>() {
            @Override
            public void subscribe(ObservableEmitter<Resource<PageFile>> emitter) throws Exception {
                File cache = getPageCache(pageNumber);
                PageFile pageFile = null;
                if (cache != null && cache.exists() && cache.isFile()) {
                    pageFile = new PageFile(pageNumber, cache.getAbsolutePath());
                }
                if (pageFile != null) {
                    emitter.onNext(Resource.success(pageFile));
                } else {
                    pageFile = new PageFile(pageNumber, null);
                    emitter.onNext(Resource.loading(pageFile));
                    try {
                        Page page = mDocumentBinding.getDocument().loadPage(pageNumber);
                        Rect bounds = page.getBounds();
                        float width = bounds.x1 - bounds.x0;
                        float height = bounds.y1 - bounds.y0;
                        if (mDocumentBinding.getPageWidth() == 0 || mDocumentBinding.getPageHeight() == 0) {
                            mDocumentBinding.setPageWidth((int) width);
                            mDocumentBinding.setPageHeight((int) height);
                        }

                        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
                        Matrix matrix = AndroidDrawDevice.fitPageWidth(page, dm.widthPixels);
                        Rect fbox = page.getBounds().transform(matrix);
                        RectI ibox = new RectI((int) fbox.x0, (int) fbox.y0, (int) fbox.x1, (int) fbox.y1);
                        int w = ibox.x1 - ibox.x0;
                        int h = ibox.y1 - ibox.y0;

                        Bitmap mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);


                        AndroidDrawDevice dev = new AndroidDrawDevice(mBitmap, ibox.x0, ibox.y0);
                        page.run(dev, matrix, (Cookie) null);
                        dev.close();
                        dev.destroy();


                        if (mBitmap != null) {
                            FileOutputStream fos = new FileOutputStream(getPageCache(pageNumber));
                            mBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                            fos.close();
                            Link[] links = page.getLinks();
                            if (links != null) {
                                for (Link link : links) {
                                    link.bounds.transform(matrix);
                                }
                            }
                            pageFile.setLinks(links);
                            mBitmap.recycle();
                            page.destroy();
                            emitter.onNext(Resource.success(pageFile));
                        } else {
                            emitter.onNext(Resource.error("页面加载失败", pageFile));
                        }
                    } catch (Throwable t) {
                        t.printStackTrace();
                        emitter.onNext(Resource.error(t.getMessage(), pageFile));
                    }
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Resource<PageFile>>() {
                    Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(Resource<PageFile> pageFileResource) {
                        result.setValue(pageFileResource);
                        if (pageFileResource.isSuccessful()) {
                            mLoadingPage = -1;
                            if (pageFileResource.getData().getPageNumber() != mCurrentPage) {
                                loadPage(mCurrentPage);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mLoadingPage = -1;
                        result.setValue(Resource.error(e.getMessage(), new PageFile(pageNumber, null)));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return result;
    }

    private File getPageCache(int pageNumber) {
        if (mDocumentBinding.getPageWidth() == 0 || mDocumentBinding.getPageWidth() == 0) {
            return null;
        }
//        return new File(mCacheDirectory, mDocumentBinding.getMd5() + String.format("%04x%04d%04d", pageNumber, (int) mDocumentBinding.getPageWidth() * 2, (int) mDocumentBinding.getPageHeight() * 2) + ".tmp");
        return new File(mCacheDirectory, mDocumentBinding.getMd5() + String.format("%04x%4d", pageNumber, mContext.getResources().getDisplayMetrics().widthPixels) + ".tmp");
    }

    public File getPage(int pageNumber) {
        File file = getPageCache(pageNumber);
        return (file != null && file.exists()) ? file : null;
    }

}
