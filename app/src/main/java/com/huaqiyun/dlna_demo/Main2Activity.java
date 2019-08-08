package com.huaqiyun.dlna_demo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.huaqiyun.dlna.Config;
import com.huaqiyun.dlna.control.callback.ControlCallback;
import com.huaqiyun.dlna.control.callback.ControlReceiveCallback;
import com.huaqiyun.dlna.entity.ClingDevice;
import com.huaqiyun.dlna.entity.DLANPlayState;
import com.huaqiyun.dlna.entity.IDevice;
import com.huaqiyun.dlna.entity.IResponse;
import com.huaqiyun.dlna.listener.DeviceListChangedListener;
import com.huaqiyun.dlna.my.controller.play.DLNARemotePlayerState;
import com.huaqiyun.dlna.my.controller.play.IDLNAPlayerController;
import com.huaqiyun.dlna.my.manager.IDLNAManager;
import com.huaqiyun.dlna.my.manager.IDeviceManager;
import com.huaqiyun.dlna.my.service.DLNAPlayService;
import com.huaqiyun.dlna.service.manager.ClingManager;
import com.huaqiyun.dlna.util.Utils;

import org.fourthline.cling.model.meta.Device;

import java.util.List;

public class Main2Activity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, SeekBar.OnSeekBarChangeListener {
    private ListView mDeviceList;
    private SwipeRefreshLayout mRefreshLayout;
    private TextView mTVSelected;
    private SeekBar mSeekProgress;
    private SeekBar mSeekVolume;
    private Switch mSwitchMute;

    private ArrayAdapter<Device> mDevicesAdapter;
    private IDLNAManager mDlnaManager;
    private IDLNAPlayerController mPlayerController;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mDlnaManager = (IDLNAManager) service;
            mDevicesAdapter.addAll(mDlnaManager.getDeviceList());
            mDevicesAdapter.notifyDataSetChanged();
            mDlnaManager.setDeviceChangeListener((deviceList, changeDevice) -> {
                runOnUiThread(() -> {
                    mDevicesAdapter.clear();
                    mDevicesAdapter.addAll(deviceList);
                    mDevicesAdapter.notifyDataSetChanged();
                });
            });
            mDlnaManager.searchDevices();
            mPlayerController = mDlnaManager.getDLNAPlayerController();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
//            mDlnaManager = null;

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initView();
        Intent intent = new Intent(this, DLNAPlayService.class);
        initListeners();
        startService(intent);
        bindService(intent,serviceConnection,BIND_AUTO_CREATE);
    }

    private void initView() {
        mDeviceList = (ListView) findViewById(R.id.lv_devices);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_refresh);
        mTVSelected = (TextView) findViewById(R.id.tv_selected);
        mSeekProgress = (SeekBar) findViewById(R.id.seekbar_progress);
        mSeekVolume = (SeekBar) findViewById(R.id.seekbar_volume);
        mSwitchMute = (Switch) findViewById(R.id.sw_mute);

        mDevicesAdapter = new DevicesAdapter2(this);
        mDeviceList.setAdapter(mDevicesAdapter);

        /** 这里为了模拟 seek 效果(假设视频时间为 15s)，拖住 seekbar 同步视频时间，
         * 在实际中 使用的是片源的时间 */
        mSeekProgress.setMax(15);

        // 最大音量就是 100，不要问我为什么
        mSeekVolume.setMax(100);

        findViewById(R.id.bt_play).setOnClickListener(this::onClick);
        findViewById(R.id.bt_pause).setOnClickListener(this::onClick);
        findViewById(R.id.bt_stop).setOnClickListener(this::onClick);
    }

    private void initListeners() {
        mRefreshLayout.setOnRefreshListener(this);

        mDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 选择连接设备
                Device item = mDevicesAdapter.getItem(position);
                mDlnaManager.setSelectDevice(item);
                String selectedDeviceName = String.format(getString(R.string.selectedText), item.getDetails().getFriendlyName());
                mTVSelected.setText(selectedDeviceName);
            }
        });

        // 静音开关
        mSwitchMute.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPlayerController.setMute(isChecked);
            }
        });

        mSeekProgress.setOnSeekBarChangeListener(this);
        mSeekVolume.setOnSeekBarChangeListener(this);
    }

    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.bt_play:
                play();
                break;

            case R.id.bt_pause:
                pause();
                break;

            case R.id.bt_stop:
                stop();
                break;
        }
    }

    /**
     * 停止
     */
    private void stop() {
        mPlayerController.stop();
    }

    /**
     * 暂停
     */
    private void pause() {
        mPlayerController.pause();
    }


    /**
     * 播放视频
     */
    private void play() {
        if(mPlayerController == null){
            mPlayerController = mDlnaManager.getDLNAPlayerController();
        }
        @DLNARemotePlayerState.DLANPlayStates int currentState = mPlayerController.getRemotePlayerState();

        /**
         * 通过判断状态 来决定 是继续播放 还是重新播放
         */

        if (currentState == DLNARemotePlayerState.STOP || currentState == DLNARemotePlayerState.IDLE) {
            mPlayerController.setPlayUrl((Config.TEST_URL));

        } else {
            mPlayerController.play();
        }
    }

    @Override
    public void onRefresh() {
        mRefreshLayout.setRefreshing(true);
        mDeviceList.setEnabled(false);

        mRefreshLayout.setRefreshing(false);
//        refreshDeviceList();
        mDeviceList.setEnabled(true);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
