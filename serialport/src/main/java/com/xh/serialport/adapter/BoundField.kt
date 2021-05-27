package com.xh.serialport.adapter

import com.xh.serialport.TypeCheckException
import com.xh.serialport.helper.ByteReadHelper
import com.xh.serialport.helper.ByteWriteHelper
import kotlin.jvm.Throws

interface BoundField :Comparable<BoundField>{

     fun write(byteWriteHelper: ByteWriteHelper, any: Any)

     @Throws(TypeCheckException::class)
     fun read(byteReadHelper: ByteReadHelper, reflectiveType: ReflectiveType<*>, any: Any)

}