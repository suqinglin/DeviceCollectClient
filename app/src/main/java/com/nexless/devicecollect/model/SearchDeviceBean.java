package com.nexless.devicecollect.model;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * @date: 2019/3/20
 * @author: su qinglin
 * @description:
 */
public class SearchDeviceBean implements Comparable<SearchDeviceBean>, Parcelable {

    public BluetoothDevice device;  // 蓝牙对象
    public int rssi = -1;           // 信号
    public boolean isBounded = false;// 已配对
    public boolean isBLEDevice = false;// 是否是4.0的蓝牙
    public String bluetoothName;//蓝牙显示的名字,BluetoothDevice的null的时候,显示mac地址.还有其他的替换规则.

    public SearchDeviceBean(BluetoothDevice d, int rssi) {
        this.device = d;
        this.rssi = rssi;
        this.bluetoothName = name();
    }
    public SearchDeviceBean(BluetoothDevice d, int rssi, boolean bounded) {
        this.device = d;
        this.rssi = rssi;
        this.isBounded = bounded;
        this.bluetoothName = name();
    }

    protected SearchDeviceBean(Parcel in) {
        device = in.readParcelable(BluetoothDevice.class.getClassLoader());
        rssi = in.readInt();
        isBounded = in.readByte() != 0;
        isBLEDevice = in.readByte() != 0;
        bluetoothName = in.readString();
    }

    public static final Creator<SearchDeviceBean> CREATOR = new Creator<SearchDeviceBean>() {
        @Override
        public SearchDeviceBean createFromParcel(Parcel in) {
            return new SearchDeviceBean(in);
        }

        @Override
        public SearchDeviceBean[] newArray(int size) {
            return new SearchDeviceBean[size];
        }
    };

    private String name() {
        String name = device.getName();
        if (TextUtils.isEmpty(name)) {
            return device.getAddress();
        }
        return name;
    }

    @Override
    public int compareTo(SearchDeviceBean o) {
        int i = this.rssi - o.rssi;
        if (i == 0) {
            return i;
        } else {
            return (i / Math.abs(i)) * -1;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(device, flags);
        dest.writeInt(rssi);
        dest.writeByte((byte) (isBounded ? 1 : 0));
        dest.writeByte((byte) (isBLEDevice ? 1 : 0));
        dest.writeString(bluetoothName);
    }
}
