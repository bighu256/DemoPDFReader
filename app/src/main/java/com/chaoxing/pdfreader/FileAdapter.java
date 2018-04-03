package com.chaoxing.pdfreader;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by bighu on 2018/4/3.
 */

public class FileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private File[] mFiles;
    private OnItemClickListener onItemClickListener;


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FileViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FileViewHolder viewHolder = (FileViewHolder) holder;
        final File file = mFiles[position];
        viewHolder.mTvFile.setText(file.getName());
        viewHolder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(file);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFiles == null ? 0 : mFiles.length;
    }

    public void setFiles(File[] files) {
        mFiles = files;
    }

    static class FileViewHolder extends RecyclerView.ViewHolder {

        View mItemView;
        TextView mTvFile;

        public FileViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mTvFile = itemView.findViewById(R.id.tv_file);
        }

    }

    public interface OnItemClickListener {
        void onItemClick(File file);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
