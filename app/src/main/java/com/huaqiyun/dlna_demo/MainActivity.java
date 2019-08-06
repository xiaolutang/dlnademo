package com.huaqiyun.dlna_demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.huaqiyun.dlna.ui.BrowserActivity;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
        start();
        search();
    }


    private void start(){
    }

    private void search(){
        Log.d(TAG, "开始进行搜索");
        Intent intent = new Intent(this, BrowserActivity.class);
        startActivity(intent);
    }



}
