package com.xh.serialport.helper

import java.io.InputStream
import java.nio.ByteBuffer
import java.util.*

class EntityByteReadHelper(allocateByteSize: Int) :
    ByteReadHelper {
    private val bytes = ByteArray(allocateByteSize)
    private val byteBuffer = ByteBuffer.allocate(allocateByteSize)
    private lateinit var inputStream: InputStream

    override fun setInputStream(inputStream: InputStream) {
        this.inputStream = inputStream

    }

    override fun getBytes(length: Int): ByteArray {
        while (byteBuffer.remaining() < length) {
            if (inputStream.available() > 0) {
                val size = inputStream.read(bytes)
                byteBuffer.mark()
                byteBuffer.position(byteBuffer.limit())
                byteBuffer.limit(byteBuffer.capacity())
                byteBuffer.put(bytes, 0, size)
                byteBuffer.limit(byteBuffer.position())
                byteBuffer.reset()
                continue
            }
            Thread.sleep(50)
        }
        byteBuffer.get(bytes, 0, length)
        return Arrays.copyOf(bytes, length)
    }

    override fun beginHandle() {
        byteBuffer.flip()
    }

    override fun resetHandle() {
        byteBuffer.position(byteBuffer.limit())
        byteBuffer.limit(byteBuffer.capacity())
    }

    override fun endHandle() {
        byteBuffer.compact()
    }

    override fun clear() {
        byteBuffer.clear()
    }


}