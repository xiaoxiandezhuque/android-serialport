package com.xh.serialport.adapter

import com.xh.serialport.XhSerialPort
import com.xh.serialport.helper.ByteReadHelper
import com.xh.serialport.helper.EntityByteWriteHelper
import com.xh.serialport.reflect.TypeToken

class ReflectiveTypeAdapterFactory() : TypeAdapterFactory {


    override fun <T> create(xhSerialPort: XhSerialPort, type: TypeToken<T>): TypeAdapter<T>? {
        val raw: Class<in T> = type.getRawType()
        if (!Any::class.java.isAssignableFrom(raw)) {
            return null // it's a primitive!
        }
        val resultType = ReflectiveType.parseAnnotations(xhSerialPort, raw)

        return Adapter(resultType as ReflectiveType<T>, xhSerialPort.mByteReadHelper)
    }


    class Adapter<T>(val reflectiveType: ReflectiveType<T>, val byteReadHelper: ByteReadHelper) :
        TypeAdapter<T> {

        fun getLength(): Int {
            return reflectiveType.getLength()
        }


        override fun read(byteArray: ByteArray): T {
            val instance = reflectiveType.clazz.newInstance()!!
            for (boundFile in reflectiveType.boundFieldList) {
                boundFile.read(byteReadHelper, reflectiveType, instance)
            }
            return instance
        }

        override fun write(data: T): String {
            val byteWriteHelper = EntityByteWriteHelper()
            for (boundFile in reflectiveType.boundFieldList) {
                boundFile.field.get(data)?.also {
                    boundFile.write(byteWriteHelper,it)
                }
            }

            return byteWriteHelper.getWriteString()
        }

    }
}