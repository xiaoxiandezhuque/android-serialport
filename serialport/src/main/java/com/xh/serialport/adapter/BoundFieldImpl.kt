package com.xh.serialport.adapter

import android.text.TextUtils
import com.xh.serialport.TypeCheckException
import com.xh.serialport.WriteExceedLengthException
import com.xh.serialport.helper.ByteReadHelper
import com.xh.serialport.helper.ByteWriteHelper
import com.xh.serialport.util.ConvertUtils
import java.lang.reflect.Field
import kotlin.jvm.Throws

class BoundFieldImpl(
    val field: Field,
    val count: Int,
    val order: Int,
    val variableLenName: String = "",
    val variableLenAdd: Int = 0,
    val resultName: String,
    val typeAdapter: TypeAdapter<*>
) : BoundField {


    override fun compareTo(other: BoundField): Int {
        if (other is BoundFieldImpl) {
            return order - other.order
        }
        return 1
    }

    override fun write(byteWriteHelper: ByteWriteHelper, any: Any) {
        var hex = (typeAdapter as TypeAdapter<Any>).write(any)
        while (hex.length < count * 2) {
            hex = "0" + hex
        }
        if (hex.length > count * 2) {
            throw  WriteExceedLengthException("写数据的长度超过限定的长度")
        }
        byteWriteHelper.write(hex)
    }

    @Throws(TypeCheckException::class)
    override fun read(
        byteReadHelper: ByteReadHelper,
        reflectiveType: ReflectiveType<*>,
        bean: Any
    ) {
        var fieldLength: Int
        if (TextUtils.isEmpty(variableLenName)) {
            fieldLength = count
        } else {
            val vbf =
                reflectiveType.getVariableBoundField(variableLenName)
            val value = vbf.field.get(bean)
            when (value) {
                is String -> {
                    fieldLength =
                        ConvertUtils.hexString2Int(value) + variableLenAdd
                }
                else -> {
                    fieldLength = value as Int + variableLenAdd
                }
            }
        }
        val byteArray =
            if (Collection::class.java.isAssignableFrom(field.type)) {
                (typeAdapter as CollectionAdapter).setLength(fieldLength)
                byteArrayOf()
            } else {
                byteReadHelper.getBytes(fieldLength)
            }
        //检查是不是当前类型
        if (reflectiveType.typeField?.boundField == this &&
            !reflectiveType.checkType(ConvertUtils.bytes2HexString(byteArray))
        ) {
            throw  TypeCheckException()
        }
        typeAdapter.read(byteArray)?.also {
            field.isAccessible = true
            field.set(bean, it)
        }
    }
}