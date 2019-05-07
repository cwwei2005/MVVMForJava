package com.example.mvvmforjava.model.net;

import android.util.Log;

import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyRetrofit {
    private static MyRetrofit instance;
    private HttpApi api;

    public static MyRetrofit getInstance(){
        if (instance == null){
            synchronized (MyRetrofit.class){
                if (instance == null) instance = new MyRetrofit();
            }
        }
        return instance;
    }

    private MyRetrofit(){
        init();
    }

    public HttpApi getApi(){
        return api;
    }


    //init
    private void init() {
        OkHttpClient client = getClient();
        Retrofit retrofit = getRetrofit(client);
        api = retrofit.create(HttpApi.class);
    }

    private OkHttpClient getClient(){
        return new OkHttpClient.Builder()
                .addInterceptor(new addCookiesInterceptor())
                .addInterceptor(new getCookiesInterceptor())
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    private Retrofit getRetrofit(OkHttpClient client){
        return new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://api.douban.com/")
                .build();
    }

    private HashSet<String> cookies   = new HashSet<>();
    public class getCookiesInterceptor implements Interceptor {

        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            okhttp3.Response originalResponse = chain.proceed(chain.request());

            if (!originalResponse.headers("Set-Cookie").isEmpty()) {


                for (String header : originalResponse.headers("Set-Cookie")) {
                    cookies.add(header);
                }
            }
            return originalResponse;
        }
    }

    public class addCookiesInterceptor implements Interceptor {

        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request.Builder builder = chain.request().newBuilder();

            for (String cookie : cookies) {
                builder.addHeader("Cookie", cookie);
                Log.v("OkHttp", "Adding Header: " + cookie); // This is done so I know which headers are being added; this interceptor is used after the normal logging of OkHttp
            }

            return chain.proceed(builder.build());
        }
    }
}
