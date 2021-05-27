package com.xh.serialport.adapter

import com.xh.serialport.XhSerialPort
import com.xh.serialport.helper.ByteReadHelper
import com.xh.serialport.reflect.TypeToken
import java.lang.reflect.ParameterizedType

class ListTypeAdapterFactory() : TypeAdapterFactory {


    override fun <T> create(xhSerialPort: XhSerialPort, type: TypeToken<T>): TypeAdapter<T>? {
        val raw: Class<in T> = type.getRawType()
        if (!List::class.java.isAssignableFrom(raw)) {
            return null
        }

        val type = (type.type as ParameterizedType).actualTypeArguments[0]
        val typeAdapter = xhSerialPort.getAdapter(TypeToken.get(type))

        return Adapter<T>(
            typeAdapter as TypeAdapter<*>
        ) as TypeAdapter<T>
    }


    class Adapter<E>(val typeAdapter: TypeAdapter<*>) :
        TypeAdapter<List<E>>, CollectionAdapter {

        private var totalLength: Int = 0


        override fun read(byteArray: ByteArray): List<E> {
            val instance = arrayListOf<E>()
            val typeAdapterLength =
                (typeAdapter as ReflectiveTypeAdapterFactory.Adapter).getLength()
            var length = 0
            while (totalLength > length) {
                //对象类型，byteArray 没有用
                val bean =
                    typeAdapter.read(byteArray) as E
                length += typeAdapterLength
                instance.add(bean)
            }
            return instance
        }

        override fun write(data: List<E>): String {
            val hex = StringBuilder()
            for (bean in data) {
                hex.append((typeAdapter as TypeAdapter<E>).write(bean))
            }
            return hex.toString()
        }

        override fun setLength(length: Int) {
            this.totalLength = length
        }

    }
}