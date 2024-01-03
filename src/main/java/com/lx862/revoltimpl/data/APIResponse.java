package com.lx862.revoltimpl.data;

public class APIResponse {
    private final int statusCode;
    private final String data;

    public APIResponse(int statusCode, String data) {
        this.statusCode = statusCode;
        this.data = data;
    }

    public boolean isSuccess() {
        return statusCode >= 200 && statusCode < 300;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getData() {
        return data;
    }
}
