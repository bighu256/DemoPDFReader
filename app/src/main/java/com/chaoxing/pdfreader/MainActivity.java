package com.chaoxing.pdfreader;

import android.Manifest;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tbruyelle.rxpermissions2.RxPermissionsFragment;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    private TextView mTvPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new RxPermissions(this)
                .requestEachCombined(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(permission -> {
                    if (permission.granted) {
                        Log.i(TAG, "granted");
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        Log.i(TAG, "shouldShowRequestPermissionRationale");
                    } else {
                        Log.i(TAG, "3");
                    }
                });

        initView();
    }

    private void initView() {
        mTvPath = findViewById(R.id.tv_path);
        mTvPath.setText("PATH");
    }

    private void openDirectory() {
        File rootDic = Environment.getExternalStorageDirectory();
        String[] files = rootDic.list();
    }

}
