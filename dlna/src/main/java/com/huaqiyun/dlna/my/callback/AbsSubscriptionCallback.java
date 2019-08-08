package com.huaqiyun.dlna.my.callback;

import android.util.Log;

import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;

/**
 *
 * */
public abstract class AbsSubscriptionCallback extends SubscriptionCallback {
    protected final String TAG = getClass().getSimpleName();

    private static final int SUBSCRIPTION_DURATION_SECONDS = 3600 * 3;

    public AbsSubscriptionCallback(Service service) {
        this(service,SUBSCRIPTION_DURATION_SECONDS);
    }

    public AbsSubscriptionCallback(Service service, int requestedDurationSeconds) {
        super(service, requestedDurationSeconds);
    }

    @Override
    protected void failed(GENASubscription subscription, UpnpResponse responseStatus, Exception exception, String defaultMsg) {
        Log.e(TAG, "AVTransportSubscriptionCallback failed.");
    }

    @Override
    protected void established(GENASubscription subscription) {
    }

    @Override
    protected void eventsMissed(GENASubscription subscription, int numberOfMissedEvents) {
    }

    @Override
    protected void ended(GENASubscription subscription, CancelReason reason, UpnpResponse responseStatus) {
        Log.e(TAG, "ended");
    }
}
