package com.huaqiyun.dlna.controller.play;

import com.huaqiyun.dlna.callback.ActionCallback;

public interface IDLNAPlayerController {

    void setDlnaPlayerEventListener(AbsDLNAPlayerEventListener mDlnaPlayerEventListener);

    String getPlayUrl();

    /**
     * 播放一个新片源
     *
     * @param url   片源地址
     */
    void setPlayUrl(String url);

    /**
     * 播放
     */
    void play();

    /**
     * 暂停
     */
    void pause();

    /**
     * 停止
     */
    void stop();

    /**
     * 视频 seek
     *
     * @param pos   seek到的位置(单位:毫秒)
     */
    void seek(long pos);

    /**
     * 设置音量
     *
     * @param pos   音量值，最大为 100，最小为 0
     */
    void setVolume(int pos);

    /**
     * 设置静音
     *
     * @param desiredMute   是否静音
     */
    void setMute(boolean desiredMute);

    /**
     * 获取tv进度
     */
    void getPositionInfo();

    /**
     * 获取音量
     */
    void getVolume();

    @DLNARemotePlayerState.DLANPlayStates int getRemotePlayerState();

    interface IDLNAPlayerActionSetUrlListener{
        void onSetPlayUrlSuccess(ActionCallback actionCallback);
        void onSetPlayUrlFailed(ActionCallback actionCallback);
    }

    interface IDLNAPlayerActionPlayListener{
        void onPlaySuccess(ActionCallback actionCallback);
        void onPlayFailed(ActionCallback actionCallback);
    }

    interface IDLNAPlayerActionPauseListener{
        void onPauseSuccess(ActionCallback actionCallback);
        void onPauseFailed(ActionCallback actionCallback);
    }

    interface IDLNAPlayerActionStopListener{
        void onStopSuccess(ActionCallback actionCallback);
        void onStopFailed(ActionCallback actionCallback);
    }

    interface IDLNAPlayerActionSeekListener{
        void onSeekSuccess(ActionCallback actionCallback);
        void onSeekFailed(ActionCallback actionCallback);
    }

    interface IDLNAPlayerActionSetVolumeListener{
        void onSetVolumeSuccess(ActionCallback actionCallback);
        void onSetVolumeFailed(ActionCallback actionCallback);
    }

    interface IDLNAPlayerActionGetVolumeListener{
        void onGetVolumeSuccess(ActionCallback actionCallback);
        void onGetVolumeFailed(ActionCallback actionCallback);
        void onGetVolumeReceived(ActionCallback actionCallback);
    }

    interface IDLNAPlayerActionSetMuteListener{
        void onSetMuteSuccess(ActionCallback actionCallback);
        void onSetMuteFailed(ActionCallback actionCallback);
    }

    interface IDLNAPlayerActionGetPositionInfoListener{
        void onGetPositionInfoSuccess(ActionCallback actionCallback);
        void onGetPositionInfoFailed(ActionCallback actionCallback);
        void onGetPositionInfoReceived(ActionCallback actionCallback);
    }
}
