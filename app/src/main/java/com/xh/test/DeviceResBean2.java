package com.xh.test;

import com.xh.serialport.ByteOccupy;
import com.xh.serialport.TypeCompare;

public class DeviceResBean2 {

    @ByteOccupy(order = 1)
    public String address;

    @TypeCompare(hexString = "07")
    @ByteOccupy(order = 2)
    public int type;
    @ByteOccupy(order = 3)
    public int length;

    @ByteOccupy(order = 4, variableLenName = "length")
    public String content;

    @ByteOccupy(count = 2, order = 5)
    public String crc;


    @Override
    public String toString() {
        return "DeviceResBean{" +
                "address=" + address +
                ", type=" + type +
                ", length=" + length +
                ", content='" + content + '\'' +
                ", crc='" + crc + '\'' +
                '}';
    }
}
