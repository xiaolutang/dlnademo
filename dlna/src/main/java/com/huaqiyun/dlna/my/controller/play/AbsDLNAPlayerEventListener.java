package com.huaqiyun.dlna.my.controller.play;

public abstract class AbsDLNAPlayerEventListener implements IDLNAPlayerController.IDLNAPlayerActionSetUrlListener
        , IDLNAPlayerController.IDLNAPlayerActionPauseListener, IDLNAPlayerController.IDLNAPlayerActionPlayListener
        , IDLNAPlayerController.IDLNAPlayerActionSeekListener, IDLNAPlayerController.IDLNAPlayerActionSetMuteListener
        , IDLNAPlayerController.IDLNAPlayerActionStopListener,IDLNAPlayerController.IDLNAPlayerActionSetVolumeListener
        , IDLNAPlayerController.IDLNAPlayerActionGetVolumeListener, IDLNAPlayerController.IDLNAPlayerActionGetPositionInfoListener {

}
