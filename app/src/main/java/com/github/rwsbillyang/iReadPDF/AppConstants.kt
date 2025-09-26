package com.github.rwsbillyang.iReadPDF


import android.util.Log
import java.time.format.DateTimeFormatter

fun log(str: String) {
    if(AppConstants.DebugRender) Log.d(AppConstants.TAG, str)
}
object AppConstants {
    const val DebugRender = false
    const val TAG = "MyAPP"

    const val KEY_CURRENT= "current"

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
}
