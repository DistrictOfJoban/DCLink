package com.lx862.revoltimpl.data;

public class UserInfo {
    private final String accountName;
    private final String id;

    public UserInfo(String id, String accountName) {
        this.id = id;
        this.accountName = accountName;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getId() {
        return id;
    }
}
