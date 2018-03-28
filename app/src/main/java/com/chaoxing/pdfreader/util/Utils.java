package com.chaoxing.pdfreader.util;

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

}
