package com.renhui.audiodemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;


public class CrimeActivity extends Activity implements AGApplication.OnAgoraEngineInterface {

    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;
    private static final String TAG = "CrimeActivity";
    private RtcEngine mRtcEngine;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime);

        /***
         * 动态申请权限
         */
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO)) {

            initAgoraEngineAndJoinChannel();
        }
    }


    public boolean checkSelfPermission(String permission, int requestCode) {

        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    requestCode);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQ_ID_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initAgoraEngineAndJoinChannel();
                } else {
                    showLongToast("No permission for " + Manifest.permission.RECORD_AUDIO);
                    finish();
                }
                break;
            }

        }
    }

    public final void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }


    private void initAgoraEngineAndJoinChannel() {

        mRtcEngine = AGApplication.the().getmRtcEngine();

        AGApplication.the().setOnAgoraEngineInterface(this);
        joinChannel("'123456'");
    }

    /***
     * 左上角返回按钮的点击事件
     * @param view
     */
    public void finishClick(View view) {

        leaveChannel();
    }


    @Override
    public void onUserJoined(final int uid, int elapsed) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 当有用户加入时，添加到用户列表
                // 当有用户加入时，添加到用户列表
                // 注意：由于demo缺少业务服务器，所以当观众加入的时候，观众也会被加入用户列表，并在界面的列表显示成静音状态。 正式实现的话，通过业务服务器可以判断是参与游戏的玩家还是围观观众
//                mUserList.add(new User(uid, ConstantApp.ARR_NAMES[new Random().nextInt(ConstantApp.ARR_NAMES.length)],
//                        ConstantApp.ARR_IMAGES[new Random().nextInt(ConstantApp.ARR_IMAGES.length)], false));
//                mAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onUserOffline(final int uid, int reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 当用户离开时，从用户列表中清除
//                mUserList.remove(getUserIndex(uid));
//                mAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onUserMuteAudio(final int uid, final boolean muted) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 收到某个uid mute 状态后刷新人员列表
//                int index = getUserIndex(uid);
//                mUserList.get(index).setAudioMute(muted);
//                mAdapter.notifyDataSetChanged();
            }
        });

    }


    @Override
    public void onJoinChannelSuccess(final String channel, int uid, int elapsed) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /**
                 * 注意：
                 * 1. 由于demo欠缺业务服务器，所以用户列表是根据 IRtcEngineEventHandler 的 onUserJoined、onUserOffline 回调来管理的
                 * 2. 每次加入频道成功后，清除列表，重新刷新用户数据
                 */
//
//                mUserList.clear();
//                if (mAdapter != null)
//                    mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onAudioVolumeIndication(final IRtcEngineEventHandler.AudioVolumeInfo[] speakers, int totalVolume) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                if (speakers != null) {
//                    for (IRtcEngineEventHandler.AudioVolumeInfo audioVolumeInfo : speakers) {
//                        if (audioVolumeInfo.volume > 0) {
//                            if (audioVolumeInfo.uid != 0) {
//                                int index = getUserIndex(audioVolumeInfo.uid);
//                                if (index >= 0) {
//                                    mUserList.get(index).setSpeaking(true);
//                                }
//                            }
//                        }
//                    }
//                    mAdapter.notifyDataSetChanged();
//                }
            }
        });


    }

//    private AudioTrack audioTrack;
//    private AudioRecord audioRecord;
//    private boolean isRecording;
//
//    private FileInputStream fileInputStream;
//
//    /**
//     * 播放，使用stream模式
//     */
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    private void playInModeStream() {
//        /*
//         * SAMPLE_RATE_INHZ 对应pcm音频的采样率
//         * channelConfig 对应pcm音频的声道
//         * AUDIO_FORMAT 对应pcm音频的格式
//         * */
//        int channelConfig = AudioFormat.CHANNEL_OUT_MONO;
//        final int minBufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE_INHZ, channelConfig, AUDIO_FORMAT);
//        audioTrack = new AudioTrack(
//                new AudioAttributes.Builder()
//                        .setUsage(AudioAttributes.USAGE_MEDIA)
//                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                        .build(),
//                new AudioFormat.Builder().setSampleRate(SAMPLE_RATE_INHZ)
//                        .setEncoding(AUDIO_FORMAT)
//                        .setChannelMask(channelConfig)
//                        .build(),
//                minBufferSize,
//                AudioTrack.MODE_STREAM,
//                AudioManager.AUDIO_SESSION_ID_GENERATE);
//        audioTrack.play();
//
//        File file = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test.pcm");
//
//        Log.e(TAG, "playInModeStream file=" + file.getAbsolutePath());
//
//        try {
//            fileInputStream = new FileInputStream(file);
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        byte[] tempBuffer = new byte[minBufferSize];
//                        while (fileInputStream.available() > 0) {
//                            int readCount = fileInputStream.read(tempBuffer);
//                            if (readCount == AudioTrack.ERROR_INVALID_OPERATION ||
//                                    readCount == AudioTrack.ERROR_BAD_VALUE) {
//                                continue;
//                            }
//                            if (readCount != 0 && readCount != -1) {
//                                audioTrack.write(tempBuffer, 0, readCount);
//                            }
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    public void startRecord(byte[] bytes) {
////        final int minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT);
////        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_INHZ,
////                CHANNEL_CONFIG, AUDIO_FORMAT, minBufferSize);
////
////        final byte data[] = new byte[minBufferSize];
//        final File file = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test.pcm");
//
//
////        if (!file.mkdirs()) {
////            Log.e(TAG, "Directory not created");
////        }
////        if (file.exists()) {
////            Log.e(TAG, "Directory exists delete");
////            file.delete();
////        }
////
////        Log.e(TAG, "file=" + file.getAbsolutePath());
////
////        audioRecord.startRecording();
////        isRecording = true;
//
//        // TODO: 2018/3/10 pcm数据无法直接播放，保存为WAV格式。
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Log.i(TAG, "run: Thread=" + file.getAbsolutePath());
//
//                FileOutputStream os = null;
//                try {
//                    os = new FileOutputStream(file);
//                    os.write(bytes);
//                } catch (FileNotFoundException e) {
//                    Log.i(TAG, "run: FileNotFoundException=" + e.getMessage());
//
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    Log.i(TAG, "run: write stream" + e.getMessage());
//
//                    e.printStackTrace();
//                } finally {
//                    if (os != null) {
//                        try {
//                            os.close();
//                        } catch (IOException e) {
//                            Log.i(TAG, "run: close file " + e.getMessage());
//
//                            e.printStackTrace();
//                        }
//                    }
//                }
//
//
////                if (null != os) {
////                    while (isRecording) {
//////                        int read = audioRecord.read(data, 0, minBufferSize);
////                        // 如果读取音频数据没有出现错误，就将数据写入到文件
////                        if (AudioRecord.ERROR_INVALID_OPERATION != read) {
////                            try {
////                                os.write(bytes);
////                            } catch (IOException e) {
////                                e.printStackTrace();
////                            }
////                        }
////                    }
////                    try {
////                        Log.i(TAG, "run: close file output stream !");
////                        os.close();
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////                }
//            }
//        }).start();
//    }


    @Override
    public boolean onRecordFrame(byte[] bytes, int numOfSamples, int bytesPerSample, int channels, int samplesPerSec) {
//        startRecord(bytes);
//        AudioTrackManager.write(bytes);
        WebSocketManager.send(bytes);
        return false;
    }

    @Override
    public boolean onPlaybackFrame(byte[] bytes, int numOfSamples, int bytesPerSample, int channels, int samplesPerSec) {
        return false;
    }


//    private int getUserIndex(int uid) {
//        for (int i = 0; i < mUserList.size(); i++) {
//            if (mUserList.get(i).getUid() == uid) {
//                return i;
//            }
//        }
//        return -1;
//    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        leaveChannel();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        /** 不是从场景界面返回时，需要重新加入大厅的群聊频道，重新设置activity setOnAgoraEngineInterface 回调注册 **/
//        if (requestCode == ConstantApp.REQUEST_CODE && resultCode == 0) {
//            if (mRtcEngine != null) {
//                AGApplication.the().setOnAgoraEngineInterface(this);
//                joinChannel(ConstantApp.CHANNEL_NAME_MAIN);
//            }
//        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        // 全局共用mute状态，在其他activity 返回时需要刷新当前mute 状态
//        if (mCheckBoxAudio != null) {
//            mCheckBoxAudio.setChecked(ConstantApp.LOCAL_AUDIO_MUTE);
//            mCheckBoxMicphone.setChecked(ConstantApp.LOCAL_MICPHONE_MUTE);
//
//        }
    }

    /**
     * 离开频道，重置听筒及麦克风按钮状态
     **/
    private void leaveChannel() {

        if (mRtcEngine != null) {
            mRtcEngine.leaveChannel();
            Log.d("azhansy", "leaveChannel=");

        }
//        ConstantApp.LOCAL_AUDIO_MUTE = false;
//        ConstantApp.LOCAL_MICPHONE_MUTE = false;
        finish();
    }

    private void joinChannel(String channelName) {
        if (mRtcEngine != null) {
            /** 模式默认用通信模式 通信场景偏好流畅**/
            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);
            // 通信模式下默认为听筒，demo中将它切为外放
            mRtcEngine.setDefaultAudioRoutetoSpeakerphone(true);
            mRtcEngine.enableAudioVolumeIndication(1000, 3, true);
            mRtcEngine.joinChannel(null, channelName, "", 0);
            Log.d("azhansy", "joinChannel=" + channelName);
        }
    }

    public static void launch(Context context) {
        Intent intent = new Intent(context, CrimeActivity.class);
        context.startActivity(intent);
    }
}
