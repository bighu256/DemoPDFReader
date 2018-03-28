package com.chaoxing.pdfreader;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by bighu on 2018/3/28.
 */

public class Resource<T> {

    @NonNull
    private final Status status;

    @Nullable
    private final String message;

    @Nullable
    private final T data;

    private Progress progress;

    private Resource(@NonNull Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> Resource<T> success(@Nullable T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    public static <T> Resource<T> error(String msg, @Nullable T data) {
        return new Resource<>(Status.ERROR, data, msg);
    }

    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(Status.LOADING, data, null);
    }

    public Progress getProgress() {
        return progress;
    }

    public Resource setProgress(Progress progress) {
        this.progress = progress;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Resource<?> resource = (Resource<?>) o;

        if (status != resource.status) {
            return false;
        }
        if (message != null ? !message.equals(resource.message) : resource.message != null) {
            return false;
        }
        return data != null ? data.equals(resource.data) : resource.data == null;
    }

    @Override
    public int hashCode() {
        int result = status.hashCode();
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {

        return new StringBuilder()
                .append("Resource{ ")
                .append("status=").append(status)
                .append(", message='").append(message)
                .append("', data=").append(data)
                .append(" }")
                .toString();
    }
}
