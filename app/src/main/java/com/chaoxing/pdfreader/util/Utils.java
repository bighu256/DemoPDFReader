package com.chaoxing.pdfreader.util;

import android.content.Context;

import java.io.File;

/**
 * Created by bighu on 2018/3/29.
 */

public class Utils {

    public static boolean isBlank(String str) {
        return str == null || str.trim().length() == 0;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static int dp2px(Context context, int dp) {
        final float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    public static String getFileExtension(File file) {
        String fileName = file.getName();
        int index = fileName.lastIndexOf('.');
        if (index >= 0) {
            return fileName.substring(index + 1).trim().toLowerCase();
        }
        return "";
    }

}
