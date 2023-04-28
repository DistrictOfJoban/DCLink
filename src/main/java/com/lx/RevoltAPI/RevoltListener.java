package com.lx.RevoltAPI;

import com.lx.RevoltAPI.data.UserInfo;

public interface RevoltListener {
    void onReady(UserInfo info);
    void onMessage();
}
