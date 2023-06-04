package com.lx.RevoltAPI.events;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lx.RevoltAPI.data.WSResponse;
import okhttp3.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class WebsocketClient extends WebSocketListener {
    private String wsUrl;
    private final List<Consumer<WSResponse>> messageCallback;
    private WebSocket ws = null;

    public WebsocketClient(String url) {
        try {
            this.wsUrl = url;
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.messageCallback = new ArrayList<>();
    }

    public void sendMessage(JsonElement jsonElement) {
        this.ws.send(jsonElement.toString());
    }

    public void start() {
        Request request = new Request.Builder().url(wsUrl).build();
        this.ws = new OkHttpClient().newWebSocket(request, this);
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        System.out.println("WS Opened");
        System.out.println(response.code());
    }

    public void onMessageCallback(Consumer<WSResponse> callback) {
        this.messageCallback.add(callback);
    }

    @Override
    public void onMessage(WebSocket webSocket, String message) {
        System.out.println(message);
        for(Consumer<WSResponse> callback : messageCallback) {
            callback.accept(new WSResponse(new JsonParser().parse(message).getAsJsonObject()));
        }
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        System.out.println("WS Closed for " + reason);
        System.out.println(code);
        ws = null;
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable throwable, Response response) {
        System.out.println("WS Error:");
        throwable.printStackTrace();
    }
}
