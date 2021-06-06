package com.codesample.mymemo.server;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.http.Header;

public class HeaderInterceptor implements Interceptor {

    private final Context context;

    public HeaderInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public @NonNull Response intercept(Chain chain) throws IOException {
        String token = context.getSharedPreferences("auth", Context.MODE_PRIVATE).getString("token", null);
        Request originalRequest = chain.request();
        if (token != null) {
            Request request = originalRequest.newBuilder()
                    .addHeader("Authorization", token)
                    .build();
            return chain.proceed(request);
        }else
        return chain.proceed(originalRequest);
    }
}
