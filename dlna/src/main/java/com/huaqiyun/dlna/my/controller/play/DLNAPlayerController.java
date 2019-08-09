package com.huaqiyun.dlna.my.controller.play;

import android.util.Log;

import com.huaqiyun.dlna.my.callback.ActionCallback;
import com.huaqiyun.dlna.my.callback.GetVolumeActionCallback;
import com.huaqiyun.dlna.my.manager.DeviceManager;
import com.huaqiyun.dlna.my.manager.IDeviceManager;
import com.huaqiyun.dlna.util.Utils;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.avtransport.callback.GetPositionInfo;
import org.fourthline.cling.support.avtransport.callback.Pause;
import org.fourthline.cling.support.avtransport.callback.Play;
import org.fourthline.cling.support.avtransport.callback.Seek;
import org.fourthline.cling.support.avtransport.callback.SetAVTransportURI;
import org.fourthline.cling.support.avtransport.callback.Stop;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.ProtocolInfo;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.item.VideoItem;
import org.fourthline.cling.support.renderingcontrol.callback.GetVolume;
import org.fourthline.cling.support.renderingcontrol.callback.SetMute;
import org.fourthline.cling.support.renderingcontrol.callback.SetVolume;
import org.seamless.util.MimeType;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DLNAPlayerController implements IDLNAPlayerController {
    private static final String TAG = DLNAPlayerController.class.getSimpleName();

    private static final String DIDL_LITE_FOOTER = "</DIDL-Lite>";
    private static final String DIDL_LITE_HEADER = "<?xml version=\"1.0\"?>" + "<DIDL-Lite " + "xmlns=\"urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/\" " +
            "xmlns:dc=\"http://purl.org/dc/elements/1.1/\" " + "xmlns:upnp=\"urn:schemas-upnp-org:metadata-1-0/upnp/\" " +
            "xmlns:dlna=\"urn:schemas-dlna-org:metadata-1-0/\">";

    private AbsDLNAPlayerEventListener mDlnaPlayerEventListener;

    private @DLNARemotePlayerState.DLANPlayStates int remotePlayerState = DLNARemotePlayerState.IDLE;

    private IDeviceManager mDeviceManager;

    public DLNAPlayerController(IDeviceManager mDeviceManager) {
        this.mDeviceManager = mDeviceManager;
    }


    public void setmDlnaPlayerEventListener(AbsDLNAPlayerEventListener mDlnaPlayerEventListener) {
        this.mDlnaPlayerEventListener = mDlnaPlayerEventListener;
    }

    @Override
    public void setPlayUrl(String url) {
        Device device = mDeviceManager.getSelectedDevice();
        if(device == null){
            return;
        }
        Service service = device.findService(DeviceManager.AV_TRANSPORT_SERVICE);
        //这个是干嘛的?
        String metadata = pushMediaToRender(url, "id", "name", "0");
        mDeviceManager.execute(new SetAVTransportURI(service,url,metadata) {
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                if(mDlnaPlayerEventListener != null){
                    mDlnaPlayerEventListener.onSetPlayUrlFailed(new ActionCallback(invocation,operation,defaultMsg));
                }
            }

            @Override
            public void success(ActionInvocation invocation) {
                super.success(invocation);
                mDeviceManager.registerAVTransport();
                mDeviceManager.registerRenderingControl();
                if(mDlnaPlayerEventListener != null){
                    mDlnaPlayerEventListener.onSetPlayUrlSuccess(new ActionCallback(invocation));
                }
            }
        });
    }

    private String pushMediaToRender(String url, String id, String name, String duration) {
        long size = 0;
        long bitrate = 0;
        Res res = new Res(new MimeType(ProtocolInfo.WILDCARD, ProtocolInfo.WILDCARD), size, url);

        String creator = "unknow";
        String resolution = "unknow";
        VideoItem videoItem = new VideoItem(id, "0", name, creator, res);

        String metadata = createItemMetadata(videoItem);
        Log.e(TAG, "metadata: " + metadata);
        return metadata;
    }

    private String createItemMetadata(DIDLObject item) {
        StringBuilder metadata = new StringBuilder();
        metadata.append(DIDL_LITE_HEADER);

        metadata.append(String.format("<item id=\"%s\" parentID=\"%s\" restricted=\"%s\">", item.getId(), item.getParentID(), item.isRestricted() ? "1" : "0"));

        metadata.append(String.format("<dc:title>%s</dc:title>", item.getTitle()));
        String creator = item.getCreator();
        if (creator != null) {
            creator = creator.replaceAll("<", "_");
            creator = creator.replaceAll(">", "_");
        }
        metadata.append(String.format("<upnp:artist>%s</upnp:artist>", creator));

        metadata.append(String.format("<upnp:class>%s</upnp:class>", item.getClazz().getValue()));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date now = new Date();
        String time = sdf.format(now);
        metadata.append(String.format("<dc:date>%s</dc:date>", time));

        // metadata.append(String.format("<upnp:album>%s</upnp:album>",
        // item.get);

        // <res protocolInfo="http-get:*:audio/mpeg:*"
        // resolution="640x478">http://192.168.1.104:8088/Music/07.我醒著做夢.mp3</res>

        Res res = item.getFirstResource();
        if (res != null) {
            // protocol info
            String protocolinfo = "";
            ProtocolInfo pi = res.getProtocolInfo();
            if (pi != null) {
                protocolinfo = String.format("protocolInfo=\"%s:%s:%s:%s\"", pi.getProtocol(), pi.getNetwork(), pi.getContentFormatMimeType(), pi
                        .getAdditionalInfo());
            }
            Log.e(TAG, "protocolinfo: " + protocolinfo);

            // resolution, extra info, not adding yet
            String resolution = "";
            if (res.getResolution() != null && res.getResolution().length() > 0) {
                resolution = String.format("resolution=\"%s\"", res.getResolution());
            }

            // duration
            String duration = "";
            if (res.getDuration() != null && res.getDuration().length() > 0) {
                duration = String.format("duration=\"%s\"", res.getDuration());
            }

            // res begin
            //            metadata.append(String.format("<res %s>", protocolinfo)); // no resolution & duration yet
            metadata.append(String.format("<res %s %s %s>", protocolinfo, resolution, duration));

            // url
            String url = res.getValue();
            metadata.append(url);

            // res end
            metadata.append("</res>");
        }
        metadata.append("</item>");

        metadata.append(DIDL_LITE_FOOTER);

        return metadata.toString();
    }

    @Override
    public void play() {
        Device device = mDeviceManager.getSelectedDevice();
        if(device == null){
            return;
        }
        Service service = device.findService(DeviceManager.AV_TRANSPORT_SERVICE);
        mDeviceManager.execute(new Play(service) {
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                remotePlayerState = DLNARemotePlayerState.ERROR;
                if(mDlnaPlayerEventListener != null){
                    mDlnaPlayerEventListener.onPlayFailed(new ActionCallback(invocation,operation,defaultMsg));
                }
            }

            @Override
            public void success(ActionInvocation invocation) {
                super.success(invocation);
                remotePlayerState = DLNARemotePlayerState.PLAY;
                if(mDlnaPlayerEventListener != null){
                    mDlnaPlayerEventListener.onPlaySuccess(new ActionCallback(invocation));
                }
            }
        });
    }

    @Override
    public void pause() {
        Device device = mDeviceManager.getSelectedDevice();
        if(device == null){
            return;
        }
        Service service = device.findService(DeviceManager.AV_TRANSPORT_SERVICE);
        mDeviceManager.execute(new Pause(service) {
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                if(mDlnaPlayerEventListener != null){
                    mDlnaPlayerEventListener.onPauseFailed(new ActionCallback(invocation,operation,defaultMsg));
                }
            }

            @Override
            public void success(ActionInvocation invocation) {
                super.success(invocation);
                remotePlayerState = DLNARemotePlayerState.PAUSE;
                if(mDlnaPlayerEventListener != null){
                    mDlnaPlayerEventListener.onPauseSuccess(new ActionCallback(invocation));
                }
            }
        });
    }

    @Override
    public void stop() {
        Device device = mDeviceManager.getSelectedDevice();
        if(device == null){
            return;
        }
        Service service = device.findService(DeviceManager.AV_TRANSPORT_SERVICE);
        mDeviceManager.execute(new Stop(service) {
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                if(mDlnaPlayerEventListener != null){
                    mDlnaPlayerEventListener.onStopFailed(new ActionCallback(invocation,operation,defaultMsg));
                }
            }

            @Override
            public void success(ActionInvocation invocation) {
                super.success(invocation);
                remotePlayerState = DLNARemotePlayerState.STOP;
                if(mDlnaPlayerEventListener != null){
                    mDlnaPlayerEventListener.onStopSuccess(new ActionCallback(invocation));
                }
            }
        });
    }

    @Override
    public void seek(long pos) {
        Device device = mDeviceManager.getSelectedDevice();
        if(device == null){
            return;
        }
        Service service = device.findService(DeviceManager.AV_TRANSPORT_SERVICE);
        String time = Utils.getStringTime((int) pos);
        Log.e(TAG, "seek->pos: " + pos + ", time: " + time);
        mDeviceManager.execute(new Seek(service,time) {
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                if(mDlnaPlayerEventListener != null){
                    mDlnaPlayerEventListener.onSeekFailed(new ActionCallback(invocation,operation,defaultMsg));
                }
            }

            @Override
            public void success(ActionInvocation invocation) {
                super.success(invocation);
                if(mDlnaPlayerEventListener != null){
                    mDlnaPlayerEventListener.onSeekSuccess(new ActionCallback(invocation));
                }
            }
        });
    }

    @Override
    public void setVolume(int pos) {
        Device device = mDeviceManager.getSelectedDevice();
        if(device == null){
            return;
        }
        Service service = device.findService(DeviceManager.AV_TRANSPORT_SERVICE);
        mDeviceManager.execute(new SetVolume(service,pos) {
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                if(mDlnaPlayerEventListener != null){
                    mDlnaPlayerEventListener.onSetVolumeFailed(new ActionCallback(invocation,operation,defaultMsg));
                }
            }

            @Override
            public void success(ActionInvocation invocation) {
                super.success(invocation);
                if(mDlnaPlayerEventListener != null){
                    mDlnaPlayerEventListener.onSetVolumeSuccess(new ActionCallback(invocation));
                }
            }
        });
    }

    @Override
    public void getVolume() {
        Device device = mDeviceManager.getSelectedDevice();
        if(device == null){
            return;
        }
        Service service = device.findService(DeviceManager.AV_TRANSPORT_SERVICE);
        mDeviceManager.execute(new GetVolume(service) {
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                if(mDlnaPlayerEventListener != null){
                    mDlnaPlayerEventListener.onGetVolumeFailed(new ActionCallback(invocation,operation,defaultMsg));
                }
            }

            @Override
            public void success(ActionInvocation invocation) {
                super.success(invocation);
                if(mDlnaPlayerEventListener != null){
                    mDlnaPlayerEventListener.onGetVolumeSuccess(new ActionCallback(invocation));
                }
            }

            @Override
            public void received(ActionInvocation actionInvocation, int currentVolume) {
                if(mDlnaPlayerEventListener != null){
                    mDlnaPlayerEventListener.onGetVolumeReceived(new GetVolumeActionCallback(actionInvocation,currentVolume));
                }
            }
        });
    }

    @Override
    public @DLNARemotePlayerState.DLANPlayStates int getRemotePlayerState() {
        return remotePlayerState;
    }

    @Override
    public void setMute(boolean desiredMute) {
        Device device = mDeviceManager.getSelectedDevice();
        if(device == null){
            return;
        }
        Service service = device.findService(DeviceManager.RENDERING_CONTROL_SERVICE);
        mDeviceManager.execute(new SetMute(service,desiredMute) {
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                if(mDlnaPlayerEventListener != null){
                    mDlnaPlayerEventListener.onSetMuteFailed(new ActionCallback(invocation,operation,defaultMsg));
                }
            }

            @Override
            public void success(ActionInvocation invocation) {
                super.success(invocation);
                if(mDlnaPlayerEventListener != null){
                    mDlnaPlayerEventListener.onSetMuteSuccess(new ActionCallback(invocation));
                }
            }
        });
    }

    @Override
    public void getPositionInfo() {
        Device device = mDeviceManager.getSelectedDevice();
        if(device == null){
            return;
        }
        Service service = device.findService(DeviceManager.AV_TRANSPORT_SERVICE);
        mDeviceManager.execute(new GetPositionInfo(service) {
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                if(mDlnaPlayerEventListener != null){
                    mDlnaPlayerEventListener.onGetPositionInfoFailed(new ActionCallback(invocation,operation,defaultMsg));
                }
            }

            @Override
            public void success(ActionInvocation invocation) {
                super.success(invocation);
                if(mDlnaPlayerEventListener != null){
                    mDlnaPlayerEventListener.onGetPositionInfoSuccess(new ActionCallback(invocation));
                }
            }

            @Override
            public void received(ActionInvocation invocation, PositionInfo positionInfo) {
                if(mDlnaPlayerEventListener != null){
                    mDlnaPlayerEventListener.onGetPositionInfoReceived(new ActionCallback(invocation));
                }
            }
        });
    }

}
