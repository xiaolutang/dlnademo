package com.huaqiyun.dlna.service;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.huaqiyun.dlna.controller.play.DLNAPlayerController;
import com.huaqiyun.dlna.controller.play.IDLNAPlayerController;
import com.huaqiyun.dlna.manager.DeviceManager;
import com.huaqiyun.dlna.manager.IDLNAManager;
import com.huaqiyun.dlna.manager.IDeviceManager;

import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.model.meta.Device;

import java.util.ArrayList;
import java.util.List;

/**
 * 考虑到有可能退出在进入需要获取相关的状态，将投屏播放放在Service中进行处理，通过bind的方式来处理
 * */
public class DLNAPlayService extends AndroidUpnpServiceImpl implements IDeviceManager.OnDeviceChangeListener {
    private final String TAG = DLNAPlayService.class.getSimpleName();

    protected IDeviceManager mDeviceManager;
    protected IDLNAPlayerController mPlayerController;
    protected Device mSelectDevice;
    protected DLNAManager mDlnaManager = new DLNAManager();


    @Override
    public void onCreate() {
        Log.d(TAG,"onCreate");
        super.onCreate();
        mDeviceManager = DeviceManager.getInstance(upnpService);
        mDeviceManager.setDeviceChangeListener(DLNAPlayService.this);
        mPlayerController = new DLNAPlayerController(mDeviceManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if(mDeviceManager == null){
            mDeviceManager = DeviceManager.getInstance(upnpService);
            mDeviceManager.setDeviceChangeListener(DLNAPlayService.this);
        }
        if(mPlayerController == null){
            mPlayerController = new DLNAPlayerController(mDeviceManager);
        }
        return mDlnaManager;
    }

    @Override
    public void onDeviceChange(List<Device> deviceList, Device changeDevice) {
        if(mDlnaManager != null && mDlnaManager.deviceChangeListener != null){
            mDlnaManager.deviceChangeListener.onDeviceChange(deviceList,changeDevice);
        }
    }

    public class DLNAManager extends Binder implements IDLNAManager {
        private IDeviceManager.OnDeviceChangeListener deviceChangeListener;
        @Override
        public void searchDevices() {
            if(mDeviceManager != null){
                mDeviceManager.search();
            }
        }

        @Override
        public void setSelectDevice(Device device){
            if(mDeviceManager != null && device != null){
                mSelectDevice = device;
                mDeviceManager.setSelectedDevice(device);
            }
        }

        @Override
        public Device getSelectDevice() {
            if(mSelectDevice != null){
                return mSelectDevice;
            }
            if(mDeviceManager == null){
                return null;
            }
            return mDeviceManager.getSelectedDevice();
        }

        @Override
        public ArrayList<Device> getDeviceList() {
            if(mDeviceManager == null){
                return new ArrayList<>();
            }
            return mDeviceManager.getDeviceList();
        }


        public IDLNAPlayerController getDLNAPlayerController(){
            return mPlayerController;
        }

        @Override
        public void setDeviceChangeListener(IDeviceManager.OnDeviceChangeListener deviceChangeListener) {
            this.deviceChangeListener = deviceChangeListener;
        }

        @Override
        public void destroy() {
            //是不是需要下面这个？
            mPlayerController = null;
            mDeviceManager.destroy();
            mDeviceManager = null;
        }
    }
}
