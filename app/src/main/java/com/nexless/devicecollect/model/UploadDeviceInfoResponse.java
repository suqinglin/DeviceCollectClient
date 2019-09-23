package com.nexless.devicecollect.model;

/**
 * @date: 2019/6/5
 * @author: su qinglin
 * @description:
 */
public class UploadDeviceInfoResponse {

    /**
     * mac : DC2C28000005
     * remark : GWF_
     */

    private String token;

//    private String uuid;

    private String model;

//    public String getUuid() {
//        return uuid;
//    }
//
//    public void setUuid(String uuid) {
//        this.uuid = uuid;
//    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
