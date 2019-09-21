package com.nexless.devicecollect.model;

/**
 * @date: 2019/6/10
 * @author: su qinglin
 * @description:
 */
public class DeviceInfo {

    public static final String UUID = "UUID";
    public static final String MAC = "MAC";
    public static final String MODEL = "Model";
    public static final String SN = "SN";
    public static final String TIME = "Time";
    public static final String HW_VER = "HwVer";
    public static final String FW_VER = "FwVer";
    public static final String MANUFACTURE = "Manufacture";
    public static final String TOOL = "Tool";

    private String uuid;
    private String mac;
    private String model;
    private String sn;
    private long time;
    private String hwVer;
    private String fwVer;
    private int manufId;
    private int toolId;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getHwVer() {
        return hwVer;
    }

    public void setHwVer(String hwVer) {
        this.hwVer = hwVer;
    }

    public String getFwVer() {
        return fwVer;
    }

    public void setFwVer(String fwVer) {
        this.fwVer = fwVer;
    }

    public int getManufId() {
        return manufId;
    }

    public void setManufId(int manufId) {
        this.manufId = manufId;
    }

    public int getToolId() {
        return toolId;
    }

    public void setToolId(int toolId) {
        this.toolId = toolId;
    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "uuid='" + uuid + '\'' +
                ", mac='" + mac + '\'' +
                ", model='" + model + '\'' +
                ", sn='" + sn + '\'' +
                ", time=" + time +
                ", hwVer='" + hwVer + '\'' +
                ", fwVer='" + fwVer + '\'' +
                ", manufId=" + manufId +
                ", toolId=" + toolId +
                '}';
    }
}
