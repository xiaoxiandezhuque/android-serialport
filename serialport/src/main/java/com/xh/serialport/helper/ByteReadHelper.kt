package com.xh.serialport.helper

import java.io.InputStream

interface ByteReadHelper {

    fun setInputStream(inputStream: InputStream)

    fun beginHandle()
    fun resetHandle()
    fun endHandle()

    fun getBytes(length: Int): ByteArray

    fun clear()

}