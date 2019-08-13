package com.huaqiyun.dlna.callback;

import org.fourthline.cling.model.action.ActionInvocation;

public class GetVolumeActionCallback extends ActionCallback {
    private int volume;

    public GetVolumeActionCallback(ActionInvocation mActionInvocation, int volume) {
        super(mActionInvocation);
        this.volume = volume;
    }

}
