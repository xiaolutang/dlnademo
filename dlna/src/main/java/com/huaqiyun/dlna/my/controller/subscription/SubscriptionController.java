package com.huaqiyun.dlna.my.controller.subscription;

import com.huaqiyun.dlna.my.callback.AVTransportSubscriptionCallback;
import com.huaqiyun.dlna.my.callback.RenderingControlSubscriptionCallback;
import com.huaqiyun.dlna.my.manager.DeviceManager;
import com.huaqiyun.dlna.util.Utils;

import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.meta.Device;

public class SubscriptionController implements ISubscriptionController{
    private AVTransportSubscriptionCallback mAvTransportSubscriptionCallback;
    private RenderingControlSubscriptionCallback mRenderingControlSubscriptionCallback;

    @Override
    public void registerAVTransport(ControlPoint controlPoint, Device device) {
        if (Utils.isNotNull(mAvTransportSubscriptionCallback)) {
            mAvTransportSubscriptionCallback.end();
        }
        mAvTransportSubscriptionCallback = new AVTransportSubscriptionCallback(device.findService(DeviceManager.AV_TRANSPORT_SERVICE));
        controlPoint.execute(mAvTransportSubscriptionCallback);
    }

    @Override
    public void registerRenderingControl(ControlPoint controlPoint, Device device) {
        if (Utils.isNotNull(mRenderingControlSubscriptionCallback)) {
            mRenderingControlSubscriptionCallback.end();
        }
        mRenderingControlSubscriptionCallback = new RenderingControlSubscriptionCallback(device.findService(DeviceManager.RENDERING_CONTROL_SERVICE));
        controlPoint.execute(mRenderingControlSubscriptionCallback);
    }

    @Override
    public void destroy() {
        if (Utils.isNotNull(mAvTransportSubscriptionCallback)) {
            mAvTransportSubscriptionCallback.end();
        }
        if (Utils.isNotNull(mRenderingControlSubscriptionCallback)) {
            mRenderingControlSubscriptionCallback.end();
        }
    }
}
