package com.xh.serialport

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.serialport.SerialPort
import com.xh.serialport.adapter.*
import com.xh.serialport.bean.ConfigBean
import com.xh.serialport.helper.ByteReadHelper
import com.xh.serialport.helper.EntityByteReadHelper
import com.xh.serialport.reflect.TypeToken
import com.xh.serialport.util.ConvertUtils
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.lang.reflect.ParameterizedType
import java.util.concurrent.ConcurrentHashMap

class XhSerialPort {
    companion object {
        private const val CONNECTION_TIME = 1000L
        private const val AUTO_CONNECTION = 1
    }

    private var mSerialPort: SerialPort? = null
    private var mInputStream: InputStream? = null
    private var mOutputStream: OutputStream? = null

    private var isConnect = false

    private var connectionWaitingTime = CONNECTION_TIME
    private var mReadThread: ReadThread? = null

    lateinit var mByteReadHelper: ByteReadHelper
    private var mConfigBean: ConfigBean? = null

    val mCallbackList = ArrayList<Callback>()
    val mResultTypeSet = mutableSetOf<Class<*>>()
    private val typeAdapterMap = ConcurrentHashMap<TypeToken<*>, TypeAdapter<*>>()
    private val factories = mutableListOf<TypeAdapterFactory>()


    init {
        factories.add(TypeAdapters.STRING_FACTORY)
        factories.add(TypeAdapters.SHORT_FACTORY)
        factories.add(TypeAdapters.INT_FACTORY)
        factories.add(TypeAdapters.BYTEARRAY_FACTORY)
        factories.add(TypeAdapters.BYTE_FACTORY)
        factories.add(ListTypeAdapterFactory())
        factories.add(ReflectiveTypeAdapterFactory())
    }


    private var mHandler = Handler(Looper.getMainLooper(), { msg ->
        when (msg.what) {
            AUTO_CONNECTION -> {
                connect()
            }
        }
        return@Handler true
    })

    private var mSendHandler: Handler? = null
    private var mSendHandlerThread: HandlerThread? = null

    fun connect(
        configBean: ConfigBean,
        connectSucListener: (() -> Unit)? = null,
        connectFailListener: ((str: String) -> Unit)? = null,
        byteReadHelper: ByteReadHelper = EntityByteReadHelper(1024)
    ) {

        mByteReadHelper = byteReadHelper
        mConfigBean = configBean

        try {
            mSerialPort = SerialPort.newBuilder(File(configBean.path), configBean.baudrate)
                .dataBits(configBean.dataBits)
                .parity(configBean.parity)
                .stopBits(configBean.stopBits)
                .build()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (mSerialPort == null) {
            connectFailListener?.invoke("连接失败")
            isConnect = false
        } else {
            connectionWaitingTime = CONNECTION_TIME
            connectSucListener?.invoke()
            mInputStream = mSerialPort?.inputStream!!
            mOutputStream = mSerialPort?.outputStream!!
            isConnect = true
            mReadThread = ReadThread(this, mInputStream!!)
            mReadThread?.start()
            mSendHandlerThread = HandlerThread("serial port send thread")
            mSendHandlerThread?.start()
        }
        if (configBean.isAutoConnection && !isConnect) {
            if (connectionWaitingTime > CONNECTION_TIME shl configBean.connectNum) {
                connectFailListener?.invoke("自动重新连接失败，不会再次重新连接")
            } else {
                mHandler.sendEmptyMessageDelayed(AUTO_CONNECTION, connectionWaitingTime shl 1)
            }
        }
    }

    private fun connect() {
        this.connect(mConfigBean!!, byteReadHelper = mByteReadHelper!!)
    }

    fun addResultType(vararg clazzs: Class<*>) {
        mResultTypeSet.addAll(clazzs)
    }

    fun addCallBack(callback: Callback) {
        if (callback.javaClass.getAnnotation(SkipReadExecutor::class.java) != null) {
            mCallbackList.add(callback)
        } else {
            mCallbackList.add(MainCallBack(callback))
        }
    }

    fun removeCallback(callback: Callback) {
        mCallbackList.remove(callback)
    }


    fun <T> getAdapter(typeToken: TypeToken<T>): TypeAdapter<T>? {
        var typeAdapter = typeAdapterMap.get(typeToken)
        if (typeAdapter != null) {
            return typeAdapter as TypeAdapter<T>
        }
        for (factory in factories) {
            typeAdapter = factory.create(this, typeToken)
            if (typeAdapter != null) {
                typeAdapterMap.put(typeToken, typeAdapter)
                return typeAdapter
            }
        }
        return null
    }

    fun <T : Any> sendObj(bean: T) {
        if (mSendHandler == null) {
            mSendHandler = Handler(mSendHandlerThread!!.looper)
        }
        mSendHandler?.post {
            val typeAdapter = getAdapter(TypeToken.get(bean.javaClass)) as TypeAdapter<T>
            sendSync(ConvertUtils.hexString2Bytes(typeAdapter.write(bean)))
        }
    }

    fun <T : Any> sendObjSync(bean: T) {
        val typeAdapter = getAdapter(TypeToken.get(bean.javaClass)) as TypeAdapter<T>
        sendSync(ConvertUtils.hexString2Bytes(typeAdapter.write(bean)))
    }

    fun sendSync(byteArray: ByteArray) {
        mOutputStream?.write(byteArray)
        mOutputStream?.flush()
    }

    fun send(byteArray: ByteArray) {
        if (mSendHandler == null) {
            mSendHandler = Handler(mSendHandlerThread!!.looper)
        }
        mSendHandler?.post {
            mOutputStream?.write(byteArray)
            mOutputStream?.flush()
        }
    }


    fun close() {
        mReadThread?.interrupt()
        mSendHandlerThread?.quit()
        mSerialPort?.tryClose()
    }


    fun addFactory(typeAdapterFactory: TypeAdapterFactory) {
        factories.add(0, typeAdapterFactory)
    }

    fun <T> addTypeAdapter(typeAdapter: TypeAdapter<T>) {
        typeAdapterMap.put(
            TypeToken.get((typeAdapter.javaClass.genericInterfaces[0] as ParameterizedType).actualTypeArguments[0]),
            typeAdapter
        )
    }
}