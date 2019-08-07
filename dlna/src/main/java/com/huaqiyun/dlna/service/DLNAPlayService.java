package com.huaqiyun.dlna.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.huaqiyun.dlna.control.ClingPlayControl;
import com.huaqiyun.dlna.control.IPlayControl;
import com.huaqiyun.dlna.control.callback.ControlCallback;
import com.huaqiyun.dlna.control.callback.ControlReceiveCallback;

/**
 * 考虑到有可能退出在进入需要获取相关的状态，将投屏播放放在Service中进行处理，通过bind的方式来处理
 * */
public class DLNAPlayService extends Service {

    private PlayControl playControl = new PlayControl();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class PlayControl extends Binder implements IPlayControl {
        @Override
        public void playNew(String url, @Nullable ControlCallback callback) {
            playControl.playNew(url,callback);
        }

        @Override
        public void play(@Nullable ControlCallback callback) {
            playControl.play(callback);
        }

        @Override
        public void pause(@Nullable ControlCallback callback) {

        }

        @Override
        public void stop(@Nullable ControlCallback callback) {

        }

        @Override
        public void seek(int pos, @Nullable ControlCallback callback) {

        }

        @Override
        public void setVolume(int pos, @Nullable ControlCallback callback) {

        }

        @Override
        public void setMute(boolean desiredMute, @Nullable ControlCallback callback) {

        }

        @Override
        public void getPositionInfo(@Nullable ControlReceiveCallback callback) {

        }

        @Override
        public void getVolume(@Nullable ControlReceiveCallback callback) {

        }
    }
}
