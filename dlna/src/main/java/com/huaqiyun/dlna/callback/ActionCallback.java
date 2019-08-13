package com.huaqiyun.dlna.callback;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;

public class ActionCallback {
    protected ActionInvocation mActionInvocation;
    protected UpnpResponse operation;
    protected String defaultMsg;

    public ActionCallback(ActionInvocation mActionInvocation) {
        this.mActionInvocation = mActionInvocation;
    }

    public ActionCallback(ActionInvocation mActionInvocation, UpnpResponse operation, String defaultMsg) {
        this.mActionInvocation = mActionInvocation;
        this.operation = operation;
        this.defaultMsg = defaultMsg;
    }
}
