package com.lx862.revoltimpl;

import com.lx862.revoltimpl.data.Message;
import com.lx862.dclink.data.bridge.User;

public interface RevoltListener {
    void onReady(User self);
    void onMessage(Message message);
}
