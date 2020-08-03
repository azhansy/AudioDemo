package com.renhui.audiodemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.renhui.audiodemo.GlobalConfig.AUDIO_FORMAT;
import static com.renhui.audiodemo.GlobalConfig.BUFFER_SIZE;
import static com.renhui.audiodemo.GlobalConfig.CHANNEL_CONFIG;
import static com.renhui.audiodemo.GlobalConfig.SAMPLE_RATE_INHZ;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int MY_PERMISSIONS_REQUEST = 1001;
    private static final String TAG = "jqd";

    private Button mBtnControl;
    private Button mBtnPlay;
    private Button btn_ws;

    /**
     * 需要申请的运行时权限
     */
    private String[] permissions = new String[]{
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    /**
     * 被用户拒绝的权限列表
     */
    private List<String> mPermissionList = new ArrayList<>();
    private boolean isRecording;
    private AudioRecord audioRecord;
    private Button mBtnConvert;
    private TextView tv;
    private Button btn_ag;
    private EditText edt;
    private byte[] audioData;
    private FileInputStream fileInputStream;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnControl = (Button) findViewById(R.id.btn_control);
        tv = findViewById(R.id.tv);
        edt = findViewById(R.id.edt);
        btn_ag = findViewById(R.id.btn_ag);
        mBtnControl.setOnClickListener(this);
        mBtnConvert = (Button) findViewById(R.id.btn_convert);
        mBtnConvert.setOnClickListener(this);
        mBtnPlay = (Button) findViewById(R.id.btn_play);
        btn_ws = (Button) findViewById(R.id.btn_ws);
        btn_ws.setOnClickListener(this);
        mBtnPlay.setOnClickListener(this);
        btn_ag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CrimeActivity.launch(MainActivity.this);
            }
        });
        checkPermissions();
        AudioTrackManager.intAudioTack();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_control:
                Button button = (Button) view;
                if (button.getText().toString().equals(getString(R.string.start_record))) {
                    button.setText(getString(R.string.stop_record));
                    startRecord();
                } else {
                    button.setText(getString(R.string.start_record));
                    stopRecord();
                }

                break;
            case R.id.btn_ws:
                WebSocketManager.setListener(edt.getText().toString(), new OnCallBack() {
                    @Override
                    public void output(final String text) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("azhansy", "text: " + text);
                                tv.setText("text: " + text);
                            }
                        });
                    }
                });
                break;
            case R.id.btn_convert:
                PcmToWavUtil pcmToWavUtil = new PcmToWavUtil(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT);
                File pcmFile = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test.pcm");
                File wavFile = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test.wav");
                if (!wavFile.mkdirs()) {
                    Log.e(TAG, "wavFile Directory not created");
                }
                if (wavFile.exists()) {
                    wavFile.delete();
                }
                pcmToWavUtil.pcmToWav(pcmFile.getAbsolutePath(), wavFile.getAbsolutePath());

                break;
            case R.id.btn_play:
                Button btn = (Button) view;
                String string = btn.getText().toString();
                if (string.equals(getString(R.string.start_play))) {
                    btn.setText(getString(R.string.stop_play));
//                    playInModeStream();
                    //playInModeStatic();
                } else {
                    btn.setText(getString(R.string.start_play));
                    stopPlay();
                }
                break;

            default:
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, permissions[i] + " 权限被用户禁止！");
                }
            }
            // 运行时权限的申请不是本demo的重点，所以不再做更多的处理，请同意权限申请。
        }
    }


    public void startRecord() {
        final int minBufferSize = BUFFER_SIZE;//AudioRecord.getMinBufferSize(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT);
        Log.e("azhansy", "minBufferSize=" + minBufferSize);

        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_INHZ,
                CHANNEL_CONFIG, AUDIO_FORMAT, minBufferSize);

        final byte data[] = new byte[minBufferSize];
        final File file = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test.pcm");
        if (!file.mkdirs()) {
            Log.e(TAG, "Directory not created");
        }
        if (file.exists()) {
            Log.e(TAG, "Directory exists delete");
            file.delete();
        }

        Log.e(TAG, "file=" + file.getAbsolutePath());

        audioRecord.startRecording();
        isRecording = true;

        // TODO: 2018/3/10 pcm数据无法直接播放，保存为WAV格式。

        new Thread(new Runnable() {
            @Override
            public void run() {

                FileOutputStream os = null;
                try {
                    os = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if (null != os) {
                    while (isRecording) {
                        int read = audioRecord.read(data, 0, minBufferSize);
                        // 如果读取音频数据没有出现错误，就将数据写入到文件
                        if (AudioRecord.ERROR_INVALID_OPERATION != read) {

                            Log.d("azhansy", "数据大小=" + data.length);
                            WebSocketManager.send(data);
//                            try {
//                                os.write(data);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
                        }
                    }
                    try {
                        Log.i(TAG, "run: close file output stream !");
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    public void stopRecord() {
        isRecording = false;
        // 释放资源
        if (null != audioRecord) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
            //recordingThread = null;
        }
    }


    private void checkPermissions() {
        // Marshmallow开始才用申请运行时权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(this, permissions[i]) !=
                        PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i]);
                }
            }
            if (!mPermissionList.isEmpty()) {
                String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);
                ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST);
            }
        }
    }


//    /**
//     * 播放，使用stream模式
//     */
//    private void playInModeStream() {
//        /*
//         * SAMPLE_RATE_INHZ 对应pcm音频的采样率
//         * channelConfig 对应pcm音频的声道
//         * AUDIO_FORMAT 对应pcm音频的格式
//         * */
////        int channelConfig = AudioFormat.CHANNEL_OUT_MONO;
//        final int minBufferSize = BUFFER_SIZE;//AudioTrack.getMinBufferSize(32000, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
//        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE_INHZ, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize, AudioTrack.MODE_STREAM);
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


//    /**
//     * 播放，使用static模式
//     */
//    private void playInModeStatic() {
//        // static模式，需要将音频数据一次性write到AudioTrack的内部缓冲区
//
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... params) {
//                try {
//                    InputStream in = getResources().openRawResource(R.raw.ding);
//                    try {
//                        ByteArrayOutputStream out = new ByteArrayOutputStream();
//                        for (int b; (b = in.read()) != -1; ) {
//                            out.write(b);
//                        }
//                        Log.d(TAG, "Got the data");
//                        audioData = out.toByteArray();
//                    } finally {
//                        in.close();
//                    }
//                } catch (IOException e) {
//                    Log.wtf(TAG, "Failed to read", e);
//                }
//                return null;
//            }
//
//
//            @Override
//            protected void onPostExecute(Void v) {
//                Log.i(TAG, "Creating track...audioData.length = " + audioData.length);
//
//                // R.raw.ding铃声文件的相关属性为 22050Hz, 8-bit, Mono
//                audioTrack = new AudioTrack(
//                        new AudioAttributes.Builder()
//                                .setUsage(AudioAttributes.USAGE_MEDIA)
//                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                                .build(),
//                        new AudioFormat.Builder().setSampleRate(32000)
//                                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
//                                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
//                                .build(),
//                        audioData.length,
//                        AudioTrack.MODE_STATIC,
//                        AudioManager.AUDIO_SESSION_ID_GENERATE);
//                Log.d(TAG, "Writing audio data...");
//                audioTrack.write(audioData, 0, audioData.length);
//                Log.d(TAG, "Starting playback");
//                audioTrack.play();
//                Log.d(TAG, "Playing");
//            }
//
//        }.execute();
//
//    }


    /**
     * 停止播放
     */
    private void stopPlay() {
        AudioTrackManager.stopPlay();
    }


    //==============================

    //    private long sendTime = 0L;
    // 发送心跳包
    private Handler mHandler = new Handler();
    // 每隔2秒发送一次心跳包，检测连接没有断开
//    private static final long HEART_BEAT_RATE = 2 * 1000;
//
//    // 发送心跳包
//    private Runnable heartBeatRunnable = new Runnable() {
//        @Override
//        public void run() {
//            if (System.currentTimeMillis() - sendTime >= HEART_BEAT_RATE) {
//
//                String message = sendData();
//                mSocket.send(message);
//                sendTime = System.currentTimeMillis();
//            }
//            mHandler.postDelayed(this, HEART_BEAT_RATE); //每隔一定的时间，对长连接进行一次心跳检测
//        }
//    };


    private String sendData() {
        String jsonHead = "";
        Map<String, Object> mapHead = new HashMap<>();
        mapHead.put("qrCode", "123456");
        jsonHead = buildRequestParams(mapHead);
        Log.e("TAG", "sendData: " + jsonHead);
        return jsonHead;
    }


    public static String buildRequestParams(Object params) {
//        Gson gson=new Gson();
//        String jsonStr=gson.toJson(params);
//        return jsonStr;
        return "";
    }

    private String sendHeart() {
        String jsonHead = "";
        Map<String, Object> mapHead = new HashMap<>();
        mapHead.put("heart", "heart");
        jsonHead = buildRequestParams(mapHead);
        Log.e("TAG", "sendHeart：" + jsonHead);
        return jsonHead;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // 清除handler后，就不能再发送数据了
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        WebSocketManager.close();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }

    }

}
