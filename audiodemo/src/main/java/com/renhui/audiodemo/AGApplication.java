package com.renhui.audiodemo;

import android.app.Application;
import android.util.Log;

import io.agora.rtc.IAudioFrameObserver;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;


public class AGApplication extends Application {
    private final String TAG = AGApplication.class.getSimpleName();


    private static AGApplication mInstance;
    private RtcEngine mRtcEngine;

    public static AGApplication the() {
        return mInstance;
    }

    public AGApplication() {
        mInstance = this;
    }

    private OnAgoraEngineInterface onAgoraEngineInterface;

    //flutter 不支持 https://github.com/AgoraIO/Flutter-SDK/issues/141
    private final IAudioFrameObserver mAudioFrameObserver = new IAudioFrameObserver() {

        @Override
        public boolean onRecordFrame(byte[] bytes, int numOfSamples, int bytesPerSample, int channels, int samplesPerSec) {
            Log.d("azhansy", "onRecordFrame=" + bytes + ",numOfSamples=" + numOfSamples + ",bytesPerSample=" + bytesPerSample + ",channels=" + channels + ",samplesPerSec=" + samplesPerSec);
            if (onAgoraEngineInterface != null) {
                return onAgoraEngineInterface.onRecordFrame(bytes, numOfSamples, bytesPerSample, channels, samplesPerSec);
            }
            return false;
        }

        @Override
        public boolean onPlaybackFrame(byte[] bytes, int numOfSamples, int bytesPerSample, int channels, int samplesPerSec) {
            Log.d("azhansy", "onPlaybackFrame=" + bytes);
            if (onAgoraEngineInterface != null) {
                return onAgoraEngineInterface.onPlaybackFrame(bytes, numOfSamples, bytesPerSample, channels, samplesPerSec);
            }
            return false;
        }
    };

    /**
     * 声网频道内业务回调
     */
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {

        @Override
        public void onUserOffline(int uid, int reason) {
            Log.d("azhansy", "onUserOffline=" + uid + ",reason=" + reason);

            if (onAgoraEngineInterface != null) {
                onAgoraEngineInterface.onUserOffline(uid, reason);
            }

        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
            Log.d("azhansy", "onUserJoined=" + uid + ",elapsed=" + elapsed);

            if (onAgoraEngineInterface != null) {
                onAgoraEngineInterface.onUserJoined(uid, elapsed);
            }

        }

        @Override
        public void onUserMuteAudio(final int uid, final boolean muted) {
            Log.d("azhansy", "onUserMuteAudio=" + uid + ",muted=" + muted);

            if (onAgoraEngineInterface != null) {
                onAgoraEngineInterface.onUserMuteAudio(uid, muted);
            }

        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);
            Log.d("azhansy", "onJoinChannelSuccess=" + uid + ",elapsed=" + elapsed);
            if (onAgoraEngineInterface != null) {
                onAgoraEngineInterface.onJoinChannelSuccess(channel, uid, elapsed);
            }

        }

        @Override
        public void onRejoinChannelSuccess(String s, int uid, int elapsed) {
            super.onRejoinChannelSuccess(s, uid, elapsed);
            Log.d("azhansy", "onRejoinChannelSuccess= uid" + uid + ",elapsed=" + elapsed);

        }

        @Override
        public void onLeaveChannel(RtcStats stats) {
            Log.d("azhansy", "RtcStats=" + stats.users);

            super.onLeaveChannel(stats);
        }

        @Override
        public void onAudioVolumeIndication(AudioVolumeInfo[] speakers, int totalVolume) {
            super.onAudioVolumeIndication(speakers, totalVolume);
            Log.d("azhansy", "speakers=" + speakers.length + ",totalVolume=" + totalVolume);

            if (onAgoraEngineInterface != null) {
                onAgoraEngineInterface.onAudioVolumeIndication(speakers, totalVolume);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        setupAgoraEngine();

    }

    public RtcEngine getmRtcEngine() {
        return mRtcEngine;
    }


    /**
     * 初始化声网 RtcEngine 对象
     */
    private void setupAgoraEngine() {
        String appID = getString(R.string.private_app_id);

        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), appID, mRtcEventHandler);
            mRtcEngine.registerAudioFrameObserver(mAudioFrameObserver);
            Log.d("azhansy", "初始化完成");
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));

            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    public void setOnAgoraEngineInterface(OnAgoraEngineInterface onAgoraEngineInterface) {
        this.onAgoraEngineInterface = onAgoraEngineInterface;
        Log.d("azhansy", "onAgoraEngineInterface=" + onAgoraEngineInterface);
    }

    /**
     * 回调接口，需要接收声网SDK回调的类实现接口即可
     */
    public interface OnAgoraEngineInterface {

        void onUserJoined(int uid, int elapsed);

        void onUserOffline(int uid, int reason);

        void onUserMuteAudio(final int uid, final boolean muted);

        void onJoinChannelSuccess(String channel, int uid, int elapsed);

        void onAudioVolumeIndication(IRtcEngineEventHandler.AudioVolumeInfo[] speakers, int totalVolume);

        boolean onRecordFrame(byte[] bytes, int numOfSamples, int bytesPerSample, int channels, int samplesPerSec);

        boolean onPlaybackFrame(byte[] bytes, int numOfSamples, int bytesPerSample, int channels, int samplesPerSec);
    }

}

