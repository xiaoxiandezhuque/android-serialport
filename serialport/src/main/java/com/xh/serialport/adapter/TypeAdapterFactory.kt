package com.xh.serialport.adapter

import com.xh.serialport.XhSerialPort
import com.xh.serialport.reflect.TypeToken

interface TypeAdapterFactory {

    fun <T> create(xhSerialPort: XhSerialPort,typeToken: TypeToken<T>): TypeAdapter<T>?
}