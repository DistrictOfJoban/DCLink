package com.lx862.revoltimpl;

import com.lx862.revoltimpl.data.Message;
import com.lx862.vendorneutral.usermember.User;

public interface RevoltListener {
    void onReady(User self);
    void onMessage(Message message);
}
