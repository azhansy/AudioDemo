package com.renhui.audiodemo;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * @author dashu
 * @date 8/3/20
 * describe:
 */
class WebSocketManager {
    private static WebSocket mSocket;

    public static void setListener(String ws, OnCallBack callBack) {
        OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                .readTimeout(3, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(3, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(3, TimeUnit.SECONDS)//设置连接超时时间
                .build();

        Request request = new Request.Builder().url(ws).build();
        EchoWebSocketListener socketListener = new EchoWebSocketListener(callBack);

        // 刚进入界面，就开启心跳检测
//        mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);

        mOkHttpClient.newWebSocket(request, socketListener);
        mOkHttpClient.dispatcher().executorService().shutdown();

    }

    public static void send(byte[] data) {
        if (mSocket != null) {
            ByteString byteString = ByteString.of(data);
            Log.d("azhansy ", "发送信息=" + byteString.hex());

            mSocket.send(byteString);
        }
    }

    public static void close() {
        if (mSocket != null) {
            mSocket.close(1000, null);
        }
    }

    private static final class EchoWebSocketListener extends WebSocketListener {
        OnCallBack callBack;

        public EchoWebSocketListener(OnCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);
            mSocket = webSocket;
//            String openid = "1";
            //连接成功后，发送登录信息
//            String message = sendData();
//            mSocket.send(message);
            callBack.output("连接成功！");


        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            super.onMessage(webSocket, bytes);
            callBack.output("receive bytes:" + bytes.hex());
            AudioTrackManager.write(bytes.toByteArray());
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
            callBack.output("服务器端发送来的信息：" + text);
            // {"msg":"付款成功","amount":"null","code":"0","qrCode":"123456","data":"cn.pay.entity.QCPOrder@3de382a5","userId":"f"}

            // 这里自己用于测试断开连接：就直接在接收到服务器发送的消息后，然后断开连接，然后清除 handler，
            //具体可以根据自己实际情况断开连接，比如点击返回键页面关闭时，执行下边逻辑
//            if (!TextUtils.isEmpty(text)){
//                if (mSocket  != null) {
//                    mSocket .close(1000, null);
//                }
//                if (mHandler != null){
//                    mHandler.removeCallbacksAndMessages(null);
//                    mHandler = null ;
//                }
//            }
            /*//收到服务器端发送来的信息后，每隔2秒发送一次心跳包
            final String message = sendHeart();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mSocket.send(message);
                }
            },2000);*/
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);
            callBack.output("closed:" + reason);
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            super.onClosing(webSocket, code, reason);
            callBack.output("closing:" + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            super.onFailure(webSocket, t, response);
            callBack.output("failure:" + t.getMessage());
        }
    }

}


