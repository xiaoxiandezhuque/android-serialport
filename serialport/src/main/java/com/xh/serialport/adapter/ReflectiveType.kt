package com.xh.serialport.adapter

import android.text.TextUtils
import com.xh.serialport.ByteOccupy
import com.xh.serialport.TypeCompare
import com.xh.serialport.XhSerialPort
import com.xh.serialport.bean.TypeField
import com.xh.serialport.reflect.TypeToken
import java.util.*

class ReflectiveType<T>(val clazz: Class<T>) {

    val boundFieldList = mutableListOf<BoundFieldImpl>()
    var typeFieldList = mutableListOf<TypeField>()


    fun getVariableBoundField(name: String): BoundFieldImpl {
        for (bean in boundFieldList) {
            if (bean.resultName == name) {
                return bean
            }
        }
        throw IllegalStateException("找不到变长的名字，请重新定义返回的实体类")
    }

    //  需要的长度,除了变长的字段
    fun getLength(): Int {
        var length = 0
        for (i in boundFieldList.indices) {
            val boundField = boundFieldList[i]
            if (TextUtils.isEmpty(boundField.variableLenName)) {
                length += boundField.count
            }
        }
        return length
    }


    companion object {
        fun <T> parseAnnotations(xhSerialPort: XhSerialPort,clazz: Class<T>): ReflectiveType<T> {
            val resultCallback = ReflectiveType(clazz)
            val fields = clazz.declaredFields
            for (i in fields.indices) {
                val byteOccupy = fields[i].getAnnotation(ByteOccupy::class.java)
                if (byteOccupy != null) {
                    val boundField = BoundFieldImpl(
                        fields[i],
                        byteOccupy.count,
                        byteOccupy.order,
                        byteOccupy.variableLenName,
                        byteOccupy.variableLenAdd,
                        fields[i].name,
                        xhSerialPort.getAdapter(TypeToken.get(fields[i].genericType))!!
                    )
                    resultCallback.boundFieldList.add(boundField)
                    val typeCompare = fields[i].getAnnotation(TypeCompare::class.java)
                    if (typeCompare != null) {
                        resultCallback.typeFieldList.add(
                            TypeField(
                                typeCompare.hexString,
                                boundField
                            )
                        )
                    }
                }
            }
            Collections.sort(resultCallback.boundFieldList)
            return resultCallback
        }


    }

}