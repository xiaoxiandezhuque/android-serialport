package com.xh.serialport

import android.os.Handler
import android.os.Looper

class MainCallBack(private val callBack: Callback) : Callback {

    private val mHandler = Handler(Looper.getMainLooper())
    override fun suc(any: Any) {
        mHandler.post {
            callBack.suc(any)
        }
    }
}