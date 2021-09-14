package com.example.newslist.data;

import androidx.annotation.Nullable;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class webSocketClient {
    private void clientWebSocket(String url) {
        OkHttpClient client = new OkHttpClient.Builder().build();
        //构造request对象
        Request request = new Request.Builder()
                .url(url)
                .build();
        //建立连接
        client.newWebSocket(request, new WebSocketListener() {
            //当远程对等方接受Web套接字并可能开始传输消息时调用。
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                webSocket.send("发送消息");
            }

            //当收到文本（类型{@code 0x1}）消息时调用
            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
            }

            //当收到二进制（类型为{@code 0x2}）消息时调用。
            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            //当远程对等体指示不再有传入的消息将被传输时调用。
            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
            }

            //当两个对等方都表示不再传输消息并且连接已成功释放时调用。 没有进一步的电话给这位听众。
            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
            }

            //由于从网络读取或向网络写入错误而关闭Web套接字时调用。
            // 传出和传入的消息都可能丢失。 没有进一步的电话给这位听众。
            @Override
            public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
            }
        });
    }


}
