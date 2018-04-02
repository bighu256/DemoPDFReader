package com.chaoxing.pdfreader;

import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.artifex.mupdf.fitz.Page;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bighu on 2018/4/1.
 */

public class PageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = PageAdapter.class.getSimpleName();

    private List<Integer> mPageIndexList;

    public PageAdapter() {
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_page, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PageViewHolder viewHolder = (PageViewHolder) holder;
//        viewHolder.mPageView.setImage(ImageSource.asset(mImageList.get(position)));
        viewHolder.mPageView.setImage(ImageSource.resource(R.mipmap.d));
    }

    @Override
    public int getItemCount() {
        return mPageIndexList == null ? 0 : mPageIndexList.size();
    }

    static class PageViewHolder extends RecyclerView.ViewHolder {

        SubsamplingScaleImageView mPageView;

        public PageViewHolder(View itemView) {
            super(itemView);
            mPageView = itemView.findViewById(R.id.page_view);
        }
    }

    public void setPageIndexList(List<Integer> pageIndexList) {
        mPageIndexList = pageIndexList;
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        int position = holder.getAdapterPosition();
        Log.i(TAG, "onViewRecycled : " + position);
    }

}
