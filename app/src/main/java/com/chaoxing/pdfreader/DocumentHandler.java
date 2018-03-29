package com.chaoxing.pdfreader;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.artifex.mupdf.fitz.Document;
import com.chaoxing.pdfreader.util.Utils;

import java.io.File;

/**
 * Created by HUWEI on 2018/3/26.
 */

public class DocumentHandler {

    private static final String TAG = DocumentHandler.class.getSimpleName();

    private static DocumentHandler sHandler;

    private DocumentHandler() {
    }

    public static DocumentHandler get() {
        if (sHandler == null) {
            synchronized (DocumentHandler.class) {
                if (sHandler == null) {
                    sHandler = new DocumentHandler();
                }
            }
        }
        return sHandler;
    }

    public LiveData<Resource<DocumentBinding>> openDocument(Context context, final String path) {
        return new ExecuteBoundResource<String, DocumentBinding>(context, path) {
            @Override
            public Resource<DocumentBinding> onExecute(Context applicationContext, String args) {

                Log.i(TAG, "open document : " + path);

                if (Utils.isBlank(args) || !new File(path).exists()) {
                    return Resource.error("打开文档失败", null);
                }

                DocumentBinding binding = new DocumentBinding();
                binding.setPath(args);
                Document document = Document.openDocument(args);
                binding.setDocument(document);
                binding.setNeedsPassword(document.needsPassword());

                return Resource.success(binding);
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
                    newDocumentBinding.setNeedsPassword(args.isNeedsPassword());
                    String title = document.getMetaData(Document.META_INFO_TITLE);
                    if (TextUtils.isEmpty(title)) {
                        title = Uri.parse(path).getLastPathSegment();
                    }
                    newDocumentBinding.setTitle(title);

                    newDocumentBinding.setReflowable(document.isReflowable());
                    if (newDocumentBinding.isReflowable()) {
//                        Log.i(TAG, "layout document");
//                        doc.layout(layoutW, layoutH, layoutEm);
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

}
