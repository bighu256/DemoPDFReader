package com.chaoxing.pdfreader;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by bighu on 2018/4/1.
 */

public class DocumentFragment extends Fragment {

    private RecyclerView mDocumentPager;
    private PageAdapter mPageAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_document, container, false);
        mDocumentPager = contentView.findViewById(R.id.document_pager);
//        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mDocumentPager.setLayoutManager(manager);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
//        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(mDocumentPager);
        mPageAdapter = new PageAdapter();
        mDocumentPager.setAdapter(mPageAdapter);
        return contentView;
    }

    private void initView() {

    }

}
