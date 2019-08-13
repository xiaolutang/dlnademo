package com.huaqiyun.dlna.manager;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.meta.Device;

import java.util.ArrayList;
import java.util.List;

public interface IDeviceManager {
    /**
     * 获取选中设备
     */
    Device getSelectedDevice();

    /**
     * 设置选中设备
     */
    void setSelectedDevice(Device selectedDevice);

    ArrayList<Device> getDeviceList();

    /**
     * 取消选中设备
     */
    void cleanSelectedDevice();

    /**
     * 添加设备
     * */
    void deviceAdded(Device device);

    /**
     * 移除设备
     * */
    void deviceRemoved(Device device);

    /**
     * 监听投屏端 AVTransport 回调
     */
    void registerAVTransport();

    /**
     * 监听投屏端 RenderingControl 回调
     */
    void registerRenderingControl();

    void search();

    void execute(ActionCallback actionCallback);

    void execute(SubscriptionCallback subscriptionCallback);

    void setDeviceChangeListener(OnDeviceChangeListener listener);

    /**
     * 销毁
     */
    void destroy();

    interface OnDeviceChangeListener{
        void onDeviceChange(List<Device> deviceList, Device changeDevice);
    }
}
