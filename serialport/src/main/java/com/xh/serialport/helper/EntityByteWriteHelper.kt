package com.xh.serialport.helper

class EntityByteWriteHelper : ByteWriteHelper {

    private var hexString = StringBuilder()

    override fun write(hexStr: String) {
        hexString.append(hexStr)
    }

    override fun getWriteString(): String {
        return hexString.toString()
    }


}