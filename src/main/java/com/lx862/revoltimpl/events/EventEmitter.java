package com.lx862.revoltimpl.events;

import com.google.gson.JsonElement;
import com.lx862.revoltimpl.RevoltListener;
import com.lx862.revoltimpl.data.Message;
import com.lx862.revoltimpl.data.WSResponse;
import com.lx862.dclink.data.bridge.User;

import java.util.ArrayList;
import java.util.List;

public class EventEmitter {
    private static final String revoltWS = "ws.revolt.chat";
    private final List<RevoltListener> listeners;
    private final WebsocketClient ws;

    public EventEmitter(String token) {
        this.listeners = new ArrayList<>();
        this.ws = new WebsocketClient("wss://" + revoltWS  + "?version=13&format=json"+ "&token=" + token);
        this.ws.onMessageCallback(this::onWebSocketMessage);
    }

    public void addListener(RevoltListener listener) {
        this.listeners.add(listener);
    }

    public void startListeningWebSocket() {
        ws.start();
    }

    private void onWebSocketMessage(WSResponse response) {
        if(response.getType().equals("Message")) {
            onChatMessage(response.getData());
        }
    }

    public void emitReadyEvent(User self) {
        for(RevoltListener listener : listeners) {
            listener.onReady(self);
        }
    }

    public void onChatMessage(JsonElement element) {
        Message message = new Message(element.getAsJsonObject());
        for(RevoltListener listener : listeners) {
            listener.onMessage(message);
        }
    }
}
