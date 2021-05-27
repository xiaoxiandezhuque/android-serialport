package com.xh.serialport.adapter

interface TypeAdapter<T> {

    fun read(byteArray: ByteArray): T

    fun write(data: T): String
}