package com.xh.test

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.LogUtils
import com.xh.serialport.Callback
import com.xh.serialport.XhSerialPort
import com.xh.serialport.bean.ConfigBean
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val xhSerialPort = XhSerialPort()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btn_send.setOnClickListener {
            val bean = WriteBean("2a", 4, 0, ContentBean(30))
            bean.setCrc()
            xhSerialPort.sendObj(bean)
        }


        xhSerialPort.addResultType(
            DeviceResBean1::class.java,
            DeviceResBean::class.java,
            DeviceResBean2::class.java
        )
        xhSerialPort.addCallBack(object : Callback {
            override fun suc(any: Any) {
                LogUtils.e(any)
            }
        })

        xhSerialPort.connect(ConfigBean("/dev/ttyS4", 9600))


    }

    override fun onDestroy() {
        super.onDestroy()

        xhSerialPort.close()

    }


}