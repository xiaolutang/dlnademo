package com.huaqiyun.dlna.my.callback;

import android.util.Log;
import com.huaqiyun.dlna.util.Utils;

import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.state.StateVariableValue;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlLastChangeParser;
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlVariable;

import java.util.Map;

public class RenderingControlSubscriptionCallback extends AbsSubscriptionCallback {
    private RenderingControlEventListener renderingControlEventListener;

    public RenderingControlSubscriptionCallback(Service service) {
        super(service);
    }

    public RenderingControlSubscriptionCallback(Service service, int requestedDurationSeconds) {
        super(service, requestedDurationSeconds);
    }

    public void setRenderingControlEventListener(RenderingControlEventListener renderingControlEventListener) {
        this.renderingControlEventListener = renderingControlEventListener;
    }

    @Override
    protected void eventReceived(GENASubscription subscription) {
        Map<String, StateVariableValue> values = subscription.getCurrentValues();
        if (Utils.isNull(values)) {
            return;
        }
        if (!values.containsKey("LastChange")) {
            return;
        }

        String lastChangeValue = values.get("LastChange").toString();
        Log.i(TAG, "LastChange:" + lastChangeValue);
        LastChange lastChange;
        try {
            lastChange = new LastChange(new RenderingControlLastChangeParser(), lastChangeValue);
            //获取音量 volume
            int volume = 0;
            if (lastChange.getEventedValue(0, RenderingControlVariable.Volume.class) != null) {

                volume = lastChange.getEventedValue(0, RenderingControlVariable.Volume.class).getValue().getVolume();

                Log.d(TAG, "onVolumeChange volume: " + volume);
                if(renderingControlEventListener != null){
                    renderingControlEventListener.onVoiceChange(volume);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface RenderingControlEventListener{
        void onVoiceChange(int voice);
    }
}
