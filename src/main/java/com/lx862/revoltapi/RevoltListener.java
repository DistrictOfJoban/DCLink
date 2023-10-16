package com.lx862.revoltapi;

import com.lx862.revoltapi.data.Message;
import com.lx862.dclink.data.bridge.User;

public interface RevoltListener {
    void onReady(User self);
    void onMessage(Message message);
}
