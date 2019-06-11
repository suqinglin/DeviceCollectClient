package com.nexless.ccommble.data.model;

/**
 * 蓝牙返回的日志
 * Created by wangkun23 on 2019/5/4.
 */
public class LockResult {

    /**
     * 返回的控制符
     */
    private String cmd;

    /**
     * 返回的结果
     */
    private String result;

    /**
     * 电池电压mV(毫伏)
     */
    private Short battery;

    /**
     * 箱包锁编号
     */
    private Integer sn;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Short getBattery() {
        return battery;
    }

    public void setBattery(Short battery) {
        this.battery = battery;
    }

    public Integer getSn() {
        return sn;
    }

    public void setSn(Integer sn) {
        this.sn = sn;
    }

    public LockResult(String cmd, String result, Short battery, Integer sn) {
        this.cmd = cmd;
        this.result = result;
        this.battery = battery;
        this.sn = sn;
    }

    @Override
    public String toString() {
        return "LockResult{" +
                "cmd='" + cmd + '\'' +
                ", result='" + result + '\'' +
                ", battery=" + battery +
                ", sn=" + sn +
                '}';
    }
}
