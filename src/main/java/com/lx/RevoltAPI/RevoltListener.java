package com.lx.RevoltAPI;

import com.lx.RevoltAPI.data.Message;
import com.lx.RevoltAPI.data.UserInfo;
import com.lx.RevoltAPI.data.accounts.User;

public interface RevoltListener {
    void onReady(User self);
    void onMessage(Message message);
}
