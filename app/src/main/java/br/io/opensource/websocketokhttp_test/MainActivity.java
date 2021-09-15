package br.io.opensource.websocketokhttp_test;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private WebSocketListener_Impl webSocketImpl;

    class WebSocketListener_Impl extends WebSocketListener {
        public WebSocketListener_Impl() {}

        @Override
        public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
            super.onClosed(webSocket, code, reason);
        }

        @Override
        public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
            webSocket.close(code, reason);
            super.onClosing(webSocket, code, reason);
        }

        @Override
        public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
            webSocket.cancel();
            super.onFailure(webSocket, t, response);
        }

        @Override
        public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
            textView.setText(text);
            webSocket.send(textView.getText().toString());
            super.onMessage(webSocket, text);
        }

        @Override
        public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
            textView.setText("It's worker in onMessage");
            webSocket.send(textView.getText().toString());
            super.onMessage(webSocket, bytes);
        }

        @Override
        public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
            textView.setText("It's worker in onOpen");
            webSocket.send(textView.getText().toString());
            super.onOpen(webSocket, response);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView)findViewById(R.id.textView1);
        OkHttpClient client = new OkHttpClient(new OkHttpClient.Builder());
        Request request = new Request.Builder()
                .url("ws://localhost:8080").build();
        client.newCall(request);

        Headers.Builder header = new Headers.Builder();
        Response response = new Response(
                request,
                Protocol.HTTP_1_0,
                "Worker",
                1000,
                null,
                header.build(),
                null,
                null,
                null,
                null,
                1000,
                500,
                null);

        webSocketImpl = new WebSocketListener_Impl();
        webSocketImpl.onOpen(client.newWebSocket(request, webSocketImpl), response);
    }
}