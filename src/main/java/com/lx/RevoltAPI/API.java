package com.lx.RevoltAPI;

import com.lx.RevoltAPI.data.APIResponse;
import net.minecraft.util.Pair;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class API {
    private final String url;
    private String token;
    private final OkHttpClient client;

    public API(String url) {
        this.url = url;
        this.client = new OkHttpClient();
    }

    public void setToken(String token) {
        this.token = token;
    }

    private Pair<String, String> getAuth() {
        return new Pair<>("X-Bot-Token", token);
    }

    public APIResponse executeGet(String endpoint) {
        try {
            String requestURL = url + endpoint;
            Pair<String, String> authHeader = getAuth();
            Request request = new Request.Builder()
                    .url(requestURL)
                    .addHeader(authHeader.getLeft(), authHeader.getRight())
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            if(response.body() != null) {
                return new APIResponse(response.code(), response.body().string());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public APIResponse executePost(String endpoint, RequestBody formData) {
        try {
            String requestURL = url + endpoint;
            Pair<String, String> authHeader = getAuth();
            Request request = new Request.Builder()
                    .url(requestURL)
                    .addHeader(authHeader.getLeft(), authHeader.getRight())
                    .post(formData)
                    .build();
            Response response = client.newCall(request).execute();
            if(response.body() != null) {
                return new APIResponse(response.code(), response.body().string());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public APIResponse executePatch(String endpoint, RequestBody formData) {
        try {
            String requestURL = url + endpoint;
            Pair<String, String> authHeader = getAuth();
            Request request = new Request.Builder()
                    .url(requestURL)
                    .addHeader(authHeader.getLeft(), authHeader.getRight())
                    .patch(formData)
                    .build();
            Response response = client.newCall(request).execute();
            if(response.body() != null) {
                return new APIResponse(response.code(), response.body().string());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
