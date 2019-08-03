package com.nexless.devicecollect.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.nexless.ccommble.codec.binary.Hex;

/**
 * 厂商信息
 */
public class ManufInfo implements Parcelable {

    private long id;

    private String name;

    private String address;

    private String password;

    private long createTime;

    private long createUserId;

    private int state;

    protected ManufInfo(Parcel in) {
        id = in.readLong();
        name = in.readString();
        address = in.readString();
        password = in.readString();
        createTime = in.readLong();
        createUserId = in.readLong();
        state = in.readInt();
    }

    public static final Creator<ManufInfo> CREATOR = new Creator<ManufInfo>() {
        @Override
        public ManufInfo createFromParcel(Parcel in) {
            return new ManufInfo(in);
        }

        @Override
        public ManufInfo[] newArray(int size) {
            return new ManufInfo[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(long createUserId) {
        this.createUserId = createUserId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(password);
        dest.writeLong(createTime);
        dest.writeLong(createUserId);
        dest.writeInt(state);
    }
}
