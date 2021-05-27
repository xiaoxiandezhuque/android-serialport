package com.xh.serialport.adapter

import com.xh.serialport.XhSerialPort
import com.xh.serialport.reflect.TypeToken
import com.xh.serialport.util.ConvertUtils

object TypeAdapters {

    val STRING = object : TypeAdapter<String> {
        override fun read(byteArray: ByteArray): String {
            if (byteArray.isEmpty()) {
                return ""
            } else {
                return ConvertUtils.bytes2HexString(byteArray)
            }
        }

        override fun write(data: String): String {
            return data
        }
    }
    val STRING_FACTORY = newFactory(String::class.java, STRING)





    val BYTEARRAY = object : TypeAdapter<ByteArray> {
        override fun read(byteArray: ByteArray): ByteArray {
            return byteArray
        }

        override fun write(data: ByteArray): String {
            return ConvertUtils.bytes2HexString(data)
        }


    }
    val BYTEARRAY_FACTORY = newFactory(ByteArray::class.java, BYTEARRAY)

    val BYTE = object : TypeAdapter<Byte> {
        override fun read(byteArray: ByteArray): Byte {
            return byteArray[0]
        }
        override fun write(data: Byte): String {
            String.format("%02x", data)
            return String.format("%02x", data)
        }
    }
    val BYTE_FACTORY = newFactory(Byte::class.java, BYTE)

    val SHORT = object : TypeAdapter<Short> {
        override fun read(byteArray: ByteArray): Short {
            return ConvertUtils.hexString2Int(ConvertUtils.bytes2HexString(byteArray)).toShort()
        }
        override fun write(data: Short): String {

            return ConvertUtils.int2HexString(data.toInt())
        }
    }
    val SHORT_FACTORY = newFactory(Short::class.java, SHORT)

    val INT = object : TypeAdapter<Int> {
        override fun read(byteArray: ByteArray): Int {
            return ConvertUtils.hexString2Int(ConvertUtils.bytes2HexString(byteArray))
        }

        override fun write(data: Int): String {
            return ConvertUtils.int2HexString(data)
        }

    }
    val INT_FACTORY = newFactory(Int::class.java, INT)


    fun <T> newFactory(clazz: Class<T>, typeAdapter: TypeAdapter<T>): TypeAdapterFactory {
        return object : TypeAdapterFactory {
            override fun <T> create(
                xhSerialPort: XhSerialPort,
                typeToken: TypeToken<T>
            ): TypeAdapter<T>? {
                return if (clazz == typeToken.rawType) typeAdapter as TypeAdapter<T> else null
            }
        }
    }
}