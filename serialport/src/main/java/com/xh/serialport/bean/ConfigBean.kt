package com.xh.serialport.bean


/**
 * @path 串口设备文件
 * @baudrate 波特率
 * @isAutoConnection 自动重连
 * @connectNum 重连次数
 * @dataBits 数据位；默认8,可选值为5~8
 * @parity 奇偶校验；0:无校验位(NONE，默认)；1:奇校验位(ODD);2:偶校验位(EVEN)
 * @stopBits 停止位；默认1；1:1位停止位；2:2位停止位
 */
data class ConfigBean(
    val path: String,
    val baudrate: Int,
    var isAutoConnection: Boolean = true,
    var connectNum: Int = 10,
    var dataBits: Int = 8,
    var parity: Int = 0,
    var stopBits: Int = 1
)