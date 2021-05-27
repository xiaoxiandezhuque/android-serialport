package com.xh.serialport

import java.lang.annotation.*
import java.lang.annotation.Retention
import java.lang.annotation.Target


/**
 * 保持在读取的线程执行
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
annotation class SkipReadExecutor