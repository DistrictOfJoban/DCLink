package com.lx.RevoltAPI;

import com.lx.RevoltAPI.data.Message;
import com.lx.dclink.data.bridge.User;

public interface RevoltListener {
    void onReady(User self);
    void onMessage(Message message);
}
