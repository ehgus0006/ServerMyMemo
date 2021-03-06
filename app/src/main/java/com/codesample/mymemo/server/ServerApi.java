package com.codesample.mymemo.server;

import com.codesample.mymemo.data.Memo;
import com.codesample.mymemo.data.User;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ServerApi {

    // 사용자 가입
    @POST("users")
    Call<Void> addUser(@Body User user);

    // 한 사용자
    @GET("users/{userid}")
    Call<User> getUser(@Path("userid") String userid);

    // 로그인
    @POST("auth/login")
    Call<User> login(@Body User user);

    // 자동 로그인
    @POST("auth/autologin")
    Call<User> autoLogin();

    // 회원 삭제
    @DELETE("/users/{userid}")
    Call<Void> deleteUser(@Path("userid") String userid);

    //메모
    // Multi-part Form Data
    @Multipart
    @POST("memos")
    Call<Void> addMemo(
            @Part("title") String title,
            @Part("content") String content,
            @Part MultipartBody.Part file
    );

    @Multipart
    @PUT("memos/{memoid}")
    Call<Void> updateMemo(
            @Path("memoid") int memoid,
            @Part("title") String title,
            @Part("content") String content,
            @Part MultipartBody.Part file
    );

    @GET("memos")
    Call<List<Memo>> getMemos();

    // RequestBody
    @GET("memos/{memoid}")
    Call<Memo> getMemo(@Path("memoid") int memoid);

    @DELETE("memos/{memoid}")
    Call<Void> deleteMemo(@Path("memoid") int memoid);
}
