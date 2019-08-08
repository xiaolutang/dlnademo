package com.huaqiyun.dlna.my.callback;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.support.model.PositionInfo;

public class GetPositionInfoActionCallback extends ActionCallback {
    private PositionInfo positionInfo;

    public GetPositionInfoActionCallback(ActionInvocation mActionInvocation, PositionInfo positionInfo) {
        super(mActionInvocation);
        this.positionInfo = positionInfo;
    }
}
