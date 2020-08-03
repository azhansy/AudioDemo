package com.awesome.websocketservice;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * Created 2020/7/29 11:12
 * Author:charcolee
 * Version:V1.0
 * ----------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------
 */
public class AudioTrackManager {

    private int  bufferSize;
    private AudioTrack audioTrack;


    private final static AudioTrackManager INSTANCE = new AudioTrackManager();

    private AudioTrackManager(){
        createStreamModeAudioTrack();
    }

    public static AudioTrackManager getInstance(){
        return INSTANCE;
    }

    /**
     * 构建 AudioTrack 实例对象
     */
    private void createStreamModeAudioTrack() {
        if (audioTrack == null) {
            bufferSize = AudioTrack.getMinBufferSize(32000, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 32000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, 640, AudioTrack.MODE_STREAM);
            audioTrack.play();
        }
    }

    public void write(byte[] buffer) {
        //PCM
        int writeResult = audioTrack.write(buffer, 0, buffer.length);
        if (writeResult >= 0) {
            //success
        } else {
            //fail
            //丢掉这一块数据
        }

    }

}
