package com.nexless.devicecollect.httpservice;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.nexless.ccommble.util.CommLog;
import com.nexless.devicecollect.util.AppPreference;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Calm on 2017/12/14.
 * HeaderInterceptor
 */

public class HeaderInterceptor implements Interceptor {

    private static final String TAG = HeaderInterceptor.class.getSimpleName();
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();
        builder.addHeader("Content-Type", "application/json");
        String token = AppPreference.getString(AppPreference.LOGIN_TOKEN, "");
        // 如果token存在，则放到请求头
        if (!TextUtils.isEmpty(token)) {
            builder.addHeader("token", token);
        }
        // 服务器只接受json格式的RequestBody，此处需将RequestBody的参数转为json之后再post
        if (request.method().equals("POST")) {
            Map<String, Object> params = new HashMap<>();
            RequestBody requestBody = request.body();
            if (requestBody instanceof FormBody) {
                FormBody oldFormBody = (FormBody) requestBody;
                for (int i = 0; i < oldFormBody.size(); i++) {
                    params.put(oldFormBody.encodedName(i), oldFormBody.encodedValue(i));
                }
                builder.post(getRequestBody(params));
            } else {
                builder.post(requestBody);
            }
        }
        return chain.proceed(builder.build());
    }

    private RequestBody getRequestBody(Map<String, Object> params) {
        String content = "";
        try {
            content = URLDecoder.decode(new Gson().toJson(params, HashMap.class), "utf-8");
            CommLog.logE(TAG, "requestBody content:" + content);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), content);
    }
}
