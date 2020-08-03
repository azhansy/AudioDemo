package com.renhui.audiodemo;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import static com.renhui.audiodemo.GlobalConfig.BUFFER_SIZE;
import static com.renhui.audiodemo.GlobalConfig.SAMPLE_RATE_INHZ;

/**
 * @author dashu
 * @date 8/3/20
 * describe:
 */
class AudioTrackManager {
    private static final String TAG = "azhansy";
    private static AudioTrack audioTrack;

    public static void intAudioTack() {
        final int minBufferSize = BUFFER_SIZE;//AudioTrack.getMinBufferSize(32000, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE_INHZ, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize, AudioTrack.MODE_STREAM);
        audioTrack.play();
        Log.e(TAG, "intAudioTack ==");

    }

    public static void write(byte[] buffer) {
        //PCM
        if (audioTrack != null) {
            int writeResult = audioTrack.write(buffer, 0, buffer.length);
        }
//        if (writeResult >= 0) {
//            //success
//        } else {
//            //fail
//            //丢掉这一块数据
//        }

    }

    /**
     * 停止播放
     */
    public static void stopPlay() {
        if (audioTrack != null) {
            Log.d(TAG, "Stopping");
            audioTrack.stop();
            Log.d(TAG, "Releasing");
            audioTrack.release();
            Log.d(TAG, "Nulling");
        }
    }
}
