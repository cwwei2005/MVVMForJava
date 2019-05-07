package com.example.mvvmforjava.model.net;

import com.example.mvvmforjava.model.db.Theater;
import com.example.mvvmforjava.model.db.Top250;

import io.reactivex.Flowable;
import retrofit2.http.GET;

public interface HttpApi {
    @GET("v2/movie/in_theaters")
    Flowable<Theater> getTheater();

    @GET("v2/movie/top250")
    Flowable<Top250> getTop250();
}
