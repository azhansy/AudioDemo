package com.awesome.websocketservice;

import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created 2020/7/29 10:33
 * Author:charcolee
 * Version:V1.0
 * ----------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------
 */
public class TestServer extends WebSocketServer {

    private static final String TAG = "TestServer";

    private Map<String,WebSocket> mWebSocket = new HashMap<>();

    public TestServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        //开始连接
        Log.d(TAG,"onOpen = "+conn.getRemoteSocketAddress().getHostName());
        mWebSocket.put(conn.getRemoteSocketAddress().getHostName(),conn);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        //服务器关闭
        Log.d(TAG,"onClose = "+conn.getRemoteSocketAddress().getHostName());
        mWebSocket.remove(conn.getRemoteSocketAddress().getHostName());
    }

    @Override
    public void onWebsocketMessageFragment(WebSocket conn, Framedata frame) {
        super.onWebsocketMessageFragment(conn, frame);
        Log.d(TAG,"onMessage onWebsocketMessageFragment = "+frame.toString());
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer buffer) {
        byte[] array = buffer.array();
//        Log.d(TAG,"onMessage array ="+ Arrays.toString(array));

        for(Map.Entry<String, WebSocket> entry : mWebSocket.entrySet()){
            String mapKey = entry.getKey();
            if (!conn.getRemoteSocketAddress().getHostName().equals(mapKey)){
                WebSocket value = entry.getValue();
                value.send(array);
            }
        }
//        AudioTrackManager.getInstance().write(array);
//        Log.d(TAG,"onMessage ByteBuffer = "+array.length);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        //接收消息，做逻辑处理，这里我直接重新返回消息
        conn.send(message);
        Log.d(TAG,"onMessage = "+message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        //异常
        Log.d(TAG,"onError = "+ex.getLocalizedMessage());
    }

    @Override
    public void onStart() {
        Log.d(TAG,"onStart = ");

    }

}
