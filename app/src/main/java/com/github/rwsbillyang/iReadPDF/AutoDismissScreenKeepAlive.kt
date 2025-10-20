package com.github.rwsbillyang.iReadPDF


import android.app.Activity
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.github.rwsbillyang.iReadPDF.pdfview.setScreenOn
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * https://yuanbao.tencent.com/chat/naQivTmsDa/58efa848-ed8e-4e0e-b082-024f5339f585
 * 自动消失的屏幕常亮Composable（支持生命周期&屏幕状态感知）
 * @param durationMs 无操作保持常亮的ms
 * @param child
 */
@Composable
fun AutoDismissScreenKeepAlive(
    durationMs: Int,
    modifier: Modifier,
     child: @Composable () -> Unit
) {
    val context = LocalContext.current
    //val rootView = LocalView.current // Compose根视图
    val activity = context as? Activity ?: return // 转换为Activity以操作窗口
    val window = activity.window
    val lifecycleOwner = LocalLifecycleOwner.current // 获取生命周期所有者

    // 状态管理
    var lastInteractionTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var idleJob by remember { mutableStateOf<Job?>(null) } // 计时器协程Job
    val scope = rememberCoroutineScope() // 协程作用域（绑定Compose生命周期）

    // ------------------------------
    // 1. 计算剩余时间（用于UI显示）
    // ------------------------------
//    val remainingSeconds by derivedStateOf {
//        val elapsed = System.currentTimeMillis() - lastInteractionTime
//        if (elapsed >= durationMs) 0 else (durationMs - elapsed) / 1000
//    }

    fun resetIdleTimer() {
        log("resetIdleTimer")
        idleJob?.cancel() // 取消之前的计时器
        lastInteractionTime = System.currentTimeMillis() // 更新最后操作时间
        window.setScreenOn(true)
        // 启动新的延迟
        idleJob = scope.launch {
            delay(durationMs.toLong())
            log("screen timer time out")
            window.setScreenOn(false)// 清除屏幕常亮标志
        }
    }
    //停止计时器（不改变屏幕常亮状态，由生命周期/屏幕状态决定是否恢复）
    fun stopIdleTimer() {
        log("stopIdleTimer")
        idleJob?.cancel()
        idleJob = null
        window.setScreenOn(false)
    }

    //启动/重置计时器（确保屏幕常亮标志被设置）
    fun startIdleTimer() {
        log("startIdleTimer")
        idleJob?.cancel()

        window.setScreenOn(true)// 设置屏幕常亮标志
        // 启动延迟任务
        idleJob = scope.launch {
            delay(durationMs.toLong())
            log("screen timer time out")
            window.setScreenOn(false) // 超时后清除屏幕常亮标志
        }
    }



    // ------------------------------
    // 3. 生命周期与屏幕状态监听
    // ------------------------------
    DisposableEffect(Unit) {
        log("enter DisposableEffect")

        // 监听生命周期变化（ON_RESUME=回到前台，ON_PAUSE=进入后台）
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    log("Lifecycle.Event.ON_RESUME")
                    startIdleTimer()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    log("Lifecycle.Event.ON_PAUSE")
                    stopIdleTimer()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)

        // 监听屏幕状态变化（息屏/唤醒）
        //会先通知lifecycle ON_RESUME/ON_PAUSE 故不需要此监听
//        val screenStateReceiver = object : BroadcastReceiver() {
//            override fun onReceive(context: Context?, intent: Intent?) {
//                when (intent?.action) {
//                    Intent.ACTION_SCREEN_ON -> {
//                        log("Intent.ACTION_SCREEN_ON")
//                        // 屏幕唤醒：若应用在前台，重新开始计时
////                        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
////                            startIdleTimer()
////                        }
//                    }
//                    Intent.ACTION_SCREEN_OFF -> {
//                        log("Intent.ACTION_SCREEN_OFF")
//                        // 屏幕息屏：停止计时
//                        //stopIdleTimer()
//                    }
//                }
//            }
//        }
//        // 注册屏幕状态广播
//        context.registerReceiver(
//            screenStateReceiver,
//            IntentFilter().apply {
//                addAction(Intent.ACTION_SCREEN_ON)
//                addAction(Intent.ACTION_SCREEN_OFF)
//            }
//        )

        // 清理逻辑（DisposableEffect退出时执行）
        onDispose {
            log("DisposableEffect.onDispose")
            stopIdleTimer() // 取消计时器
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver) // 移除生命周期监听
            //context.unregisterReceiver(screenStateReceiver) // 注销屏幕广播
        }
    }

    // ------------------------------
    // 4. 用户操作监听（重置计时器）
    // ------------------------------
    Box(modifier.pointerInput(Unit) {
                awaitEachGesture {
                    while (true) {
                        val event = awaitPointerEvent()
                        // 监听所有触摸按下事件（点击/滑动起始）
                        if (event.type == PointerEventType.Press) {
                            log("got interaction")
                            resetIdleTimer()
                        }
                    }
                }
            }
    ) {
        child()
    }
}