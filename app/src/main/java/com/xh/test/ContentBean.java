package com.xh.test;

import com.xh.serialport.ByteOccupy;

public class ContentBean {


    @ByteOccupy(count = 2, order = 1)
    public int value;

    public ContentBean(int value) {
        this.value = value;
    }

    public ContentBean() {
    }

    @Override
    public String toString() {
        return "ContentBean{" +
                "value=" + value +
                '}';
    }
}
