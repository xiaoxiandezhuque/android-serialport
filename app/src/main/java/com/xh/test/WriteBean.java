package com.xh.test;

import com.xh.serialport.ByteOccupy;

public class WriteBean {


    @ByteOccupy(order = 1)
    public String address;

    @ByteOccupy(order = 2)
    public int function;

    @ByteOccupy(order = 3, count = 2)
    public int registerStartBit;

    @ByteOccupy(order = 4, count = 2)
    public ContentBean numberOfRegisters;

    @ByteOccupy(order = 5, count = 2)
    public String crc;

    public WriteBean(String address, int function, int registerStartBit, ContentBean numberOfRegisters) {
        this.address = address;
        this.function = function;
        this.registerStartBit = registerStartBit;
        this.numberOfRegisters = numberOfRegisters;
    }

    public WriteBean() {
    }

    public void setCrc() {
        crc = "7619";

    }
}
