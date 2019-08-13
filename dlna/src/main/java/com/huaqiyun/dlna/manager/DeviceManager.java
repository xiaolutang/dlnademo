package com.huaqiyun.dlna.manager;

import android.content.Context;
import android.util.Log;

import com.huaqiyun.dlna.controller.subscription.ISubscriptionController;
import com.huaqiyun.dlna.controller.subscription.SubscriptionController;

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
import java.util.Collection;

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

    private RegistryListener registryListener = new MRegistryListener();

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
        if(deviceManager.mUpnpService != mUpnpService){
            synchronized (DeviceManager.class){
                deviceManager = new DeviceManager(mUpnpService);
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
        mUpnpService.getRegistry().addListener(registryListener);
        mDeviceList.clear();
        mDeviceList.addAll(mUpnpService.getRegistry().getDevices());
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
        mDeviceList.clear();
        Collection<Device> deviceCollection =  mUpnpService.getRegistry().getDevices();
        for (Device device: deviceCollection){
            if (!device.getType().equals(DMR_DEVICE_TYPE)) {
               break;
            }
            mDeviceList.add(device);
        }
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
        mUpnpService.getRegistry().removeAllRemoteDevices();
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
        synchronized (DeviceManager.class){
            mDeviceList.clear();
            mSubscriptionController.destroy();
            mDeviceChangeListener = null;
            mUpnpService.getRegistry().removeListener(registryListener);
            deviceManager = null;
        }
    }

    @Override
    public void deviceAdded(Device device) {

        if (!device.getType().equals(DMR_DEVICE_TYPE)) {
            Log.e(TAG, "deviceAdded called, but not match");
            return;
        }
        Log.d(TAG, "deviceAdded");
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
            Log.d(TAG,"remoteDeviceDiscoveryStarted");
        }

        @Override
        public void remoteDeviceDiscoveryFailed(Registry registry, RemoteDevice device, Exception ex) {
            Log.d(TAG,"remoteDeviceDiscoveryFailed");
            deviceRemoved(device);
        }

        @Override
        public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
            Log.d(TAG,"remoteDeviceAdded");
            deviceAdded(device);
        }

        @Override
        public void remoteDeviceUpdated(Registry registry, RemoteDevice device) {
            Log.d(TAG,"remoteDeviceUpdated");
            deviceAdded(device);
        }

        @Override
        public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
            Log.d(TAG,"remoteDeviceRemoved");
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
            Log.d(TAG,"beforeShutdown");
        }

        @Override
        public void afterShutdown() {
            Log.d(TAG,"afterShutdown");
        }
    }
}
