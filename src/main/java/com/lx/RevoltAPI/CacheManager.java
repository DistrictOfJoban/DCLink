package com.lx.RevoltAPI;

import com.lx.RevoltAPI.data.Channel;
import com.lx.RevoltAPI.data.UserInfo;

import java.util.HashMap;

public class CacheManager {
    public static UserInfo userInfo = null;
    public static HashMap<String, Channel> cachedChannel = new HashMap<>();
}
