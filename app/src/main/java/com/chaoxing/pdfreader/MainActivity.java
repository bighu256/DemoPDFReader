package com.chaoxing.pdfreader;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tbruyelle.rxpermissions2.RxPermissionsFragment;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    private MainViewModel mViewModel;
    private TextView mTvPath;
    private RecyclerView mFileList;
    private FileAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        initView();
        initDirectory();
        attemptOpenDirectory();
    }

    private void initView() {
        mTvPath = findViewById(R.id.tv_path);
        mFileList = findViewById(R.id.file_list);
        mFileList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new FileAdapter();
        mFileList.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new FileAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(File file) {
                if (file.isDirectory()) {
                    openDirectory(file);
                } else {

                }
            }
        });
    }

    private void initDirectory() {
        mViewModel.getFiles().observe(this, files -> {
            mAdapter.setFiles(files);
            mAdapter.notifyDataSetChanged();
        });
    }

    private void attemptOpenDirectory() {
        new RxPermissions(this)
                .requestEachCombined(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(permission -> {
                    if (permission.granted) {
                        openRootDirectory();
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        attemptOpenDirectory();
                    } else {
                        Toast.makeText(MainActivity.this, "获取\"存储空间\"权限失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openRootDirectory() {
        File rootDirectory = Environment.getExternalStorageDirectory();
        openDirectory(rootDirectory);
    }

    private void openDirectory(File directory) {
        if (directory != null && directory.isDirectory()) {
            mTvPath.setText(directory.getPath());
            mViewModel.openDirectory(directory);
        }
    }
}
