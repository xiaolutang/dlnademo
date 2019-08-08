package com.huaqiyun.dlna.my.controller.subscription;

import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.meta.Device;

/**
 * 用于注册TV端的回调
 * */
public interface ISubscriptionController {
    /**
     * 监听投屏端 AVTransport 回调
     */
    void registerAVTransport(ControlPoint controlPoint, Device device);

    /**
     * 监听投屏端 RenderingControl 回调
     */
    void registerRenderingControl(ControlPoint controlPoint, Device device);

    /**
     * 销毁
     * */
    void destroy();
}
