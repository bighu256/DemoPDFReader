package com.chaoxing.pdfreader;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.artifex.mupdf.fitz.Document;
import com.artifex.mupdf.fitz.Page;
import com.chaoxing.pdfreader.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HUWEI on 2018/3/26.
 */

public class DocumentHelper {

    private static final String TAG = DocumentHelper.class.getSimpleName();

    public LiveData<Resource<DocumentBinding>> openDocument(Context context, final String path) {
        return new ExecuteBoundResource<String, DocumentBinding>(context, path) {
            @Override
            public Resource<DocumentBinding> onExecute(Context applicationContext, String args) {

                Log.i(TAG, "open document : " + path);

                try {
                    if (Utils.isBlank(args) || !new File(path).exists()) {
                        return Resource.error("文档不存在", null);
                    }

                    DocumentBinding binding = new DocumentBinding();
                    binding.setPath(args);
                    Document document = Document.openDocument(args);
                    binding.setDocument(document);
                    binding.setMd5(Utils.md5(new File(path)));
                    binding.setNeedsPassword(document.needsPassword());

                    return Resource.success(binding);
                } catch (Throwable t) {
                    return Resource.error(t.getMessage(), null);
                }
            }
        }.ready().execute();
    }

    public LiveData<Resource<Boolean>> checkPassword(Context context, Document document, String password) {
        return new ExecuteBoundResource<Object[], Boolean>(context, new Object[]{document, password}) {
            @NonNull
            @Override
            public Resource<Boolean> onExecute(Context applicationContext, Object args[]) {
                return Resource.success(((Document) args[0]).authenticatePassword((String) args[1]));
            }
        }.execute();
    }

    public LiveData<Resource<DocumentBinding>> loadDocument(Context context, DocumentBinding documentBinding) {
        return new ExecuteBoundResource<DocumentBinding, DocumentBinding>(context, documentBinding) {
            @NonNull
            @Override
            protected Resource<DocumentBinding> onExecute(Context applicationContext, DocumentBinding args) {
                String message = null;
                try {

                    Log.i(TAG, "load document");

                    final String path = args.getPath();
                    final Document document = args.getDocument();
                    DocumentBinding newDocumentBinding = new DocumentBinding();
                    newDocumentBinding.setPath(path);
                    newDocumentBinding.setDocument(document);
                    newDocumentBinding.setMd5(args.getMd5());
                    newDocumentBinding.setNeedsPassword(args.isNeedsPassword());
                    String title = document.getMetaData(Document.META_INFO_TITLE);
                    String format = document.getMetaData(Document.META_FORMAT);
                    if (TextUtils.isEmpty(title)) {
                        title = Uri.parse(path).getLastPathSegment();
                    }
                    newDocumentBinding.setTitle(title);

                    newDocumentBinding.setReflowable(document.isReflowable());
                    if (newDocumentBinding.isReflowable()) {
                        DisplayMetrics dm = applicationContext.getResources().getDisplayMetrics();
                        document.layout(dm.widthPixels, dm.heightPixels, 8);
                    }

                    newDocumentBinding.setPageCount(document.countPages());

                    return Resource.success(newDocumentBinding);
                } catch (Throwable t) {
                    message = t.getMessage();
                }

                return Resource.error(message, null);
            }
        }.ready().execute();
    }

    public LiveData<Resource<List<Page>>> loadPageList(Context context, DocumentBinding documentBinding) {
        return new ExecuteBoundResource<DocumentBinding, List<Page>>(context, documentBinding) {

            @NonNull
            @Override
            protected Resource<List<Page>> onExecute(Context applicationContext, DocumentBinding args) {
                List<Page> pageList = new ArrayList<>(args.getPageCount());
                for (int i = 0; i < args.getPageCount(); i++) {
                    pageList.add(null);
                }
                return Resource.success(pageList);
            }
        }.ready().execute();

    }

}
