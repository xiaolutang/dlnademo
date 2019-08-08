package com.huaqiyun.dlna.my.manager;

import android.content.Context;
import android.util.Log;

import com.huaqiyun.dlna.my.controller.subscription.ISubscriptionController;
import com.huaqiyun.dlna.my.controller.subscription.SubscriptionController;
import com.huaqiyun.dlna.service.manager.ClingManager;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryListener;

import java.util.ArrayList;

public class DeviceManager implements IDeviceManager {
    private final String TAG = DeviceManager.class.getSimpleName();

    public static final ServiceType AV_TRANSPORT_SERVICE = new UDAServiceType("AVTransport");
    /** 控制服务 */
    public static final ServiceType RENDERING_CONTROL_SERVICE = new UDAServiceType("RenderingControl");
    public static final DeviceType DMR_DEVICE_TYPE = new UDADeviceType("MediaRenderer");

    private UpnpService mUpnpService;
    private ArrayList<Device> mDeviceList = new ArrayList<>();
    private ISubscriptionController mSubscriptionController = new SubscriptionController();
    private Device mSelectDevice;

    private OnDeviceChangeListener mDeviceChangeListener;

    private static DeviceManager deviceManager;

    public static IDeviceManager getInstance(UpnpService mUpnpService){
        if(deviceManager == null){
            synchronized (DeviceManager.class){
                if(deviceManager == null){
                    deviceManager = new DeviceManager(mUpnpService);
                }
            }
        }
        return deviceManager;
    }

    /**
     * 暂时不考虑以Context作为参数，在DeviceManager进行服务的绑定。
     * 如果进入退出后可以不管以前的状态这个也许是一个好方案。
     * 未实现完成
     * */

    private static IDeviceManager getInstance(Context context){
        if(deviceManager == null){
            synchronized (DeviceManager.class){
                if(deviceManager == null){
                    deviceManager = new DeviceManager(context);
                }
            }
        }
        return deviceManager;
    }

    private DeviceManager(Context context) {
    }

    private DeviceManager(UpnpService mUpnpService) {
        this.mUpnpService = mUpnpService;
        mUpnpService.getRegistry().addListener(new MRegistryListener());
    }

    @Override
    public Device getSelectedDevice() {
        return mSelectDevice;
    }

    @Override
    public void setSelectedDevice(Device selectedDevice) {
         mSelectDevice = selectedDevice;
    }

    @Override
    public ArrayList<Device> getDeviceList() {
        return mDeviceList;
    }

    @Override
    public void cleanSelectedDevice() {
        mSelectDevice = null;
        //同时关闭相关的客户端
    }

    @Override
    public void registerAVTransport() {
        mSubscriptionController.registerAVTransport(mUpnpService.getControlPoint(),mSelectDevice);
    }

    @Override
    public void registerRenderingControl() {
        mSubscriptionController.registerRenderingControl(mUpnpService.getControlPoint(),mSelectDevice);
    }

    @Override
    public void search() {
        mUpnpService.getControlPoint().search();
    }

    @Override
    public void execute(ActionCallback actionCallback) {
        mUpnpService.getControlPoint().execute(actionCallback);
    }

    @Override
    public void execute(SubscriptionCallback subscriptionCallback) {
        mUpnpService.getControlPoint().execute(subscriptionCallback);
    }

    @Override
    public void setDeviceChangeListener(OnDeviceChangeListener listener) {
        mDeviceChangeListener = listener;
    }

    @Override
    public void destroy() {
        mDeviceList.clear();
        mSubscriptionController.destroy();
        mDeviceChangeListener = null;
        synchronized (DeviceManager.class){
            deviceManager = null;
        }
    }

    @Override
    public void deviceAdded(Device device) {
        Log.d(TAG, "deviceAdded");
        if (!device.getType().equals(ClingManager.DMR_DEVICE_TYPE)) {
            Log.e(TAG, "deviceAdded called, but not match");
            return;
        }

        if(!mDeviceList.contains(device)){
            mDeviceList.add(device);
            if(mDeviceChangeListener != null){
                mDeviceChangeListener.onDeviceChange(mDeviceList,device);
            }
        }
    }

    public void deviceRemoved(Device device) {
        Log.d(TAG, "deviceRemoved");
       if( mDeviceList.remove(device)){
           if(mDeviceChangeListener != null){
               mDeviceChangeListener.onDeviceChange(mDeviceList,device);
           }
       }
    }

    class MRegistryListener implements RegistryListener {
        @Override
        public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {

        }

        @Override
        public void remoteDeviceDiscoveryFailed(Registry registry, RemoteDevice device, Exception ex) {
            deviceRemoved(device);
        }

        @Override
        public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
            deviceAdded(device);
        }

        @Override
        public void remoteDeviceUpdated(Registry registry, RemoteDevice device) {

        }

        @Override
        public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
            deviceRemoved(device);
        }

        @Override
        public void localDeviceAdded(Registry registry, LocalDevice device) {

        }

        @Override
        public void localDeviceRemoved(Registry registry, LocalDevice device) {

        }

        @Override
        public void beforeShutdown(Registry registry) {

        }

        @Override
        public void afterShutdown() {

        }
    }
}
