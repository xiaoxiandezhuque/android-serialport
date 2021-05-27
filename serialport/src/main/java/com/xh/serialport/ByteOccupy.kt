package com.xh.serialport


@kotlin.annotation.Target(AnnotationTarget.FIELD)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class ByteOccupy(
    val count: Int = 1,
    val order: Int,
    val variableLenName: String = "",//由哪个字段确定
    val variableLenAdd: Int = 0// 如果那个字段指总长度，可以通过这个字段修改
)