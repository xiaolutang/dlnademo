package com.huaqiyun.dlna.callback;

import android.util.Log;

import com.huaqiyun.dlna.Config;
import com.huaqiyun.dlna.util.Utils;

import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportLastChangeParser;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.model.TransportState;

import java.util.Map;

/**
 * 视频播放相关的回调
 * */
public class AVTransportSubscriptionCallback extends AbsSubscriptionCallback {
    private AVTransportSubscriptionEventListener avTransportSubscriptionEventListener;

    public AVTransportSubscriptionCallback(Service service) {
        super(service);
    }

    public AVTransportSubscriptionCallback(Service service, int requestedDurationSeconds) {
        super(service, requestedDurationSeconds);
    }

    public AVTransportSubscriptionEventListener getAvTransportSubscriptionEventListener() {
        return avTransportSubscriptionEventListener;
    }

    public void setAvTransportSubscriptionEventListener(AVTransportSubscriptionEventListener avTransportSubscriptionEventListener) {
        this.avTransportSubscriptionEventListener = avTransportSubscriptionEventListener;
    }

    @Override
    protected void eventReceived(GENASubscription subscription) {
        Map values = subscription.getCurrentValues();
        if (values != null && values.containsKey("LastChange")) {
            String lastChangeValue = values.get("LastChange").toString();
            Log.i(TAG, "LastChange:" + lastChangeValue);
            doAVTransportChange(lastChangeValue);
        }
    }

    private void doAVTransportChange(String lastChangeValue) {
        try {
            Log.d(TAG,"doAVTransportChange");
            LastChange lastChange = new LastChange(new AVTransportLastChangeParser(), lastChangeValue);

            //Parse TransportState value.
            AVTransportVariable.TransportState transportState = lastChange.getEventedValue(0, AVTransportVariable.TransportState.class);
            if (transportState != null) {
                TransportState ts = transportState.getValue();
                switch (ts){
                    case PLAYING:{
                        Log.d(TAG, "PLAYING");
                        if(avTransportSubscriptionEventListener != null){
                            avTransportSubscriptionEventListener.onPlaying();
                        }
                        return;
                    }
                    case PAUSED_PLAYBACK:{
                        Log.d(TAG, "PAUSED_PLAYBACK");
                        if(avTransportSubscriptionEventListener != null){
                            avTransportSubscriptionEventListener.onPausePlayback();
                        }
                        return;
                    }
                    case STOPPED:{
                        Log.d(TAG, "STOPPED");
                        if(avTransportSubscriptionEventListener != null){
                            avTransportSubscriptionEventListener.onStopped();
                        }
                        return;
                    }
                    case TRANSITIONING:{
                        Log.d(TAG, "BUFFER");
                        if(avTransportSubscriptionEventListener != null){
                            avTransportSubscriptionEventListener.onTransitioning();
                        }
                        return;
                    }
                }
            }

            //RelativeTimePosition
            String position = "00:00:00";
            AVTransportVariable.RelativeTimePosition eventedValue = lastChange.getEventedValue(0, AVTransportVariable.RelativeTimePosition.class);
            if (Utils.isNotNull(eventedValue)) {
                position = lastChange.getEventedValue(0, AVTransportVariable.RelativeTimePosition.class).getValue();
                int intTime = Utils.getIntTime(position);
                Log.d(TAG, "position: " + position + ", intTime: " + intTime);

                // 该设备支持进度回传
                Config.getInstance().setHasRelTimePosCallback(true);
                if(avTransportSubscriptionEventListener != null){
                    avTransportSubscriptionEventListener.onTimeUpdate(position,intTime);
                }
                // TODO: 17/7/20 ACTION_PLAY_COMPLETE 播完了

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public interface AVTransportSubscriptionEventListener{
        void onPlaying();
        void onPausePlayback();
        void onStopped();
        void onTransitioning();
        void onTimeUpdate(String formatTime, long time);
        void onComplete();
    }
}
