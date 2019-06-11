package com.nexless.devicecollect.model;

/**
 * @date: 2019/6/5
 * @author: su qinglin
 * @description:
 */
public class LoginResponse {

    /**
     * userToken :
     * eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZXhwIjoxNTYyMjU2MDAwLCJpYXQiOjE1NTk3MTY1Nzl9.EgiamymcorSIhRBZvyc57WKwssLV7-0GhDwHFpzQpto
     */

    private String userToken;

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
}
