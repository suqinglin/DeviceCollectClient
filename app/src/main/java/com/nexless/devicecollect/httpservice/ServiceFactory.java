package com.nexless.devicecollect.httpservice;

import com.nexless.devicecollect.AppConstants;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by Calm on 2017/11/24.
 * 网络请求工厂类
 */

public class ServiceFactory {
    private static ServiceFactory mInstance;
    private Retrofit mRetrofit;
    private ApiService mApiService;

    public static ServiceFactory getInstance() {
        if (mInstance == null) {
            synchronized (ServiceFactory.class) {
                if (mInstance == null) {
                    mInstance = new ServiceFactory();
                }
            }
        }
        return mInstance;
    }

    private ServiceFactory() {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.addInterceptor(new HeaderInterceptor());
        if (AppConstants.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }
        mRetrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.SERVER_URL)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public ApiService getApiService() {
        if (mApiService == null) {
            mApiService = mRetrofit.create(ApiService.class);
        }
        return mApiService;
    }
}
