package com.nexless.ccommble.data.model;

/**
 * 读取日志结果
 */
public class LogResult {

    /**
     * 命令
     */
    private String cmd;
    /**
     * 执行结果
     * 0x00 操作成功
     * 0x03 无日志
     * 0xFE 其他错误
     */
    private String result;
    /**
     * 操作员的 ID
     */
    private String userId;
    /**
     * 操作时间，秒数
     */
    private long timeStamp;
    /**
     * 箱包顺序号
     */
    private long sn;

    public LogResult() {
    }

    public LogResult(String cmd, String result, String userId, long timeStamp, long sn) {
        this.cmd = cmd;
        this.result = result;
        this.userId = userId;
        this.timeStamp = timeStamp;
        this.sn = sn;
    }

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getSn() {
        return sn;
    }

    public void setSn(long sn) {
        this.sn = sn;
    }

    @Override
    public String toString() {
        return "LogResult{" +
                "cmd='" + cmd + '\'' +
                ", result=" + result +
                ", userId='" + userId + '\'' +
                ", timeStamp=" + timeStamp +
                ", sn=" + sn +
                '}';
    }
}
