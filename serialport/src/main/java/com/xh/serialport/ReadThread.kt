package com.xh.serialport

import com.xh.serialport.reflect.TypeToken
import java.io.InputStream

class ReadThread(
    private val xhSerialPort: XhSerialPort,
    private val inputStream: InputStream,
) : Thread() {

    private val byteReadHelper = xhSerialPort.mByteReadHelper


    init {
        byteReadHelper.setInputStream(inputStream)
    }

    override fun run() {
        super.run()
        while (!isInterrupted) {
            try {
                var bean: Any? = null
                for (resultType in xhSerialPort.mResultTypeSet) {
                    val adapter = xhSerialPort.getAdapter(TypeToken.get(resultType))!!
                    try {
                        byteReadHelper.beginHandle()
                        bean = adapter.read(byteArrayOf())
                        byteReadHelper.endHandle()
                        break
                    } catch (e: TypeCheckException) {
                        byteReadHelper.resetHandle()
//                        e.printStackTrace()
                    }
                }
                if (bean != null) {
                    for (callback in xhSerialPort.mCallbackList) {
                        callback.suc(bean)
                    }
                } else {
                    byteReadHelper.beginHandle()
                    byteReadHelper.getBytes(1)
                    byteReadHelper.endHandle()
                }

            } catch (e: InterruptedException) {
                return
            } catch (e: Exception) {
                byteReadHelper.clear()
                e.printStackTrace()
            }
        }
    }
}