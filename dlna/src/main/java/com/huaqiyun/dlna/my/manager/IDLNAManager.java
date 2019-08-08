package com.huaqiyun.dlna.my.manager;

import com.huaqiyun.dlna.my.controller.play.IDLNAPlayerController;

import org.fourthline.cling.model.meta.Device;

import java.util.ArrayList;

public interface IDLNAManager {
    /**
     * 搜索所有的设备
     */
    void searchDevices();

    void setSelectDevice(Device device);

    Device getSelectDevice();

    ArrayList<Device> getDeviceList();

    IDLNAPlayerController getDLNAPlayerController();

    void setDeviceChangeListener(IDeviceManager.OnDeviceChangeListener deviceChangeListener);

    /**
     * 销毁
     */
    void destroy();
}
