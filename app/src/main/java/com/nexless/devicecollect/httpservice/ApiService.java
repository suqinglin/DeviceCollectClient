package com.nexless.devicecollect.httpservice;

import com.nexless.devicecollect.model.LoginResponse;
import com.nexless.devicecollect.model.MacAndTokenResponse;
import com.nexless.devicecollect.model.ManufListResponse;
import com.nexless.devicecollect.model.TResponse;
import com.nexless.devicecollect.model.TResponseNoData;
import com.nexless.devicecollect.model.UploadDeviceInfoResponse;
import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * @date: 2019/5/23
 * @author: su qinglin
 * @description:
 */
public interface ApiService {

    @FormUrlEncoded
    @POST("/user/login")
    Observable<TResponse<LoginResponse>> login(
            @Field("userPhone") String userPhone,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("/device/macAndToken")
    Observable<TResponse<MacAndTokenResponse>> getMacAndToken(
            @Field("uuid") String uuid
    );

    @FormUrlEncoded
    @POST("/device/uploadDeviceInfo")
    Observable<TResponse<UploadDeviceInfoResponse>> uploadDeviceInfo(
            @Field("uuid") String uuid,
            @Field("mac") String mac,
            @Field("sn") String sn,
            @Field("model") String model,
            @Field("hwVersion") String hwVersion,
            @Field("fwVersion") String fwVersion,
            @Field("manufacturer") String manufacturer,
            @Field("toolId") int toolId,
            @Field("createTime") long createTime,
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("/device/saveManyu2Lock")
    Observable<TResponseNoData> saveManyu2Lock(
            @Field("uuid") String uuid,
            @Field("model") String model,
            @Field("mac") String mac,
            @Field("token") String token,
            @Field("hwVer") String hwVer,
            @Field("fwVer") String fwVer
    );

    @FormUrlEncoded
    @POST("/device/getDeviceToken")
    Observable<TResponse<UploadDeviceInfoResponse>> getDeviceToken(
            @Field("mac") String mac
    );

    @POST("/manuf/list")
    Observable<TResponse<ManufListResponse>> getManufList();
}
