package com.xh.serialport


//确定返回当前bean的  类型
@kotlin.annotation.Target(AnnotationTarget.FIELD)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class TypeCompare(
    val hexString: String
)