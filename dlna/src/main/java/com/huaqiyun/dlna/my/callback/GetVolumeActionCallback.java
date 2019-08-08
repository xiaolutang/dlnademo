package com.huaqiyun.dlna.my.callback;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;

public class GetVolumeActionCallback extends ActionCallback {
    private int volume;

    public GetVolumeActionCallback(ActionInvocation mActionInvocation, int volume) {
        super(mActionInvocation);
        this.volume = volume;
    }

}
