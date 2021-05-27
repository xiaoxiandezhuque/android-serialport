package com.xh.serialport

//检查当前数据是不是当前的实体类的类型
class TypeCheckException : RuntimeException()
class WriteExceedLengthException(msg:String) : RuntimeException(msg)