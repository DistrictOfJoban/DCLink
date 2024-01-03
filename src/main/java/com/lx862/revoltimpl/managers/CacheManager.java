package com.lx862.revoltimpl.managers;

import com.lx862.revoltimpl.data.Channel;
import com.lx862.revoltimpl.data.Message;
import com.lx862.dclink.data.bridge.Member;
import com.lx862.dclink.data.bridge.User;

import java.util.HashMap;

public class CacheManager {
    public static User userInfo = null;
    public static HashMap<String, Channel> cachedChannel = new HashMap<>();
    public static HashMap<String, User> cachedUsers = new HashMap<>();
    public static HashMap<String, Member> cachedMembers = new HashMap<>();
    public static HashMap<String, Message> cachedMessages = new HashMap<>();
}
