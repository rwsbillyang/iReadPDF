package com.github.rwsbillyang.iReadPDF

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.Window
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.rounded.Adjust
import androidx.compose.material.icons.rounded.Landscape
import androidx.compose.material.icons.rounded.Portrait
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.github.rwsbillyang.composerouter.ScreenCall
import com.github.rwsbillyang.composerouter.useRouter
import com.github.rwsbillyang.iReadPDF.db.Book
import com.rajat.pdfviewer.port.PdfRendererView
import com.rajat.pdfviewer.port.PdfRendererViewCompose
import com.rajat.pdfviewer.port.PdfSource

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PdfViewerToolIcons() {
    val router = useRouter()
    val viewModel: MyViewModel = LocalViewModel.current
    val context = LocalContext.current
    Spacer(Modifier.width(8.dp))
    val b = viewModel.currentBook.value
    Text(
        b?.total?.let { "${b.page}/$it" } ?: "${b?.page}",
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodySmall
    )

    IconButton(onClick = { router.navByName(AppRoutes.BookShelf) }) {
        Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "book shelf")
    }

    IconButton(onClick = {
        //TODO: go to page, open a dialog
        //b?.page =
    }) {
        Icon(Icons.Rounded.Adjust, contentDescription = "go to page dialog")
    }

    IconButton(onClick = {
        (context as? Activity)?.requestedOrientation = if(b?.landscape == 1) ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }) {
        if(b?.landscape == 1)
            Icon(Icons.Rounded.Portrait, contentDescription = "portrait")
        else
            Icon(Icons.Rounded.Landscape, contentDescription = "landscape")
    }

    IconButton(onClick = { router.navByName(AppRoutes.Settings) })
    {
        Icon(Icons.Rounded.Settings, contentDescription = "Settings")
    }
}


@Composable
fun ScreenPdfViewer(call: ScreenCall) {
    val currentBook: Book? = call.props as? Book
    if(currentBook == null){
        Column(Modifier.fillMaxSize(),verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
            Text("no book to open")
        }
        return
    }

    //val context = LocalContext.current
    //val lifecycleOwner = LocalLifecycleOwner.current

    val viewModel: MyViewModel = LocalViewModel.current
    viewModel.currentBook.value = currentBook
    currentBook.lastOpen = System.currentTimeMillis()

    // 协程作用域：用于管理延迟任务
    val coroutineScope = rememberCoroutineScope()
    // 保存延迟任务的 Job：用于取消未执行的任务
    var hideUiJob by remember { mutableStateOf<Job?>(null) }
    // 当前窗口（可能为 null，需判空）
    val currentWindow = getCurrentWindow()



    // 1. 监听全屏状态变化，控制窗口UI
    // ------------------------------
    LaunchedEffect(viewModel.isFullScreen.value) {
        currentWindow?.let{window ->
            if (viewModel.isFullScreen.value) {
                // 进入全屏：隐藏系统栏（状态栏+导航栏）
                WindowCompat.setDecorFitsSystemWindows(window, false) // 让内容延伸到系统栏区域
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    hide(WindowInsetsCompat.Type.systemBars()) // 隐藏系统栏
                    //systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE // 滑动边缘唤出系统栏
                }
                // 取消之前的“恢复全屏”任务（若有）
                hideUiJob?.cancel()
                hideUiJob = null
            } else {
                // 退出全屏：显示系统栏
                WindowCompat.setDecorFitsSystemWindows(window, true) // 内容不延伸到系统栏
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    show(WindowInsetsCompat.Type.systemBars()) // 显示系统栏
                }
                // 启动3秒后自动恢复全屏的任务
                hideUiJob = coroutineScope.launch {
                    delay(5000) // 5秒无操作
                    viewModel.isFullScreen.value = true // 切换回全屏
                }
            }
        }

    }

    PdfRendererViewCompose(
        source = PdfSource.LocalUri(currentBook.uri),
        lifecycleOwner = LocalLifecycleOwner.current,
        modifier = Modifier,
        //headers = HeaderData(mapOf("Authorization" to "123456789")),
        jumpToPage = currentBook.page,
        statusCallBack = object : PdfRendererView.StatusCallBack {
            override fun onPdfLoadStart() {
                log("onPdfLoadStart")
            }
            override fun onPdfLoadProgress(progress: Int, downloadedBytes: Long, totalBytes: Long?) {
                log("onPdfLoadProgress: progress=$progress, downloadedBytes=$downloadedBytes, totalBytes=$totalBytes")
            }
            override fun onPdfLoadSuccess(absolutePath: String) {
                log("onPdfLoadSuccess: absolutePath=$absolutePath")
            }
            override fun onError(error: Throwable) {
                log("onError=${error.message}")
            }
            override fun onPageChanged(currentPage: Int, totalPage: Int) {
                currentBook.page = currentPage
                currentBook.total = totalPage
            }
            override fun onPdfRenderStart() {
                log("onPdfRenderStart")
            }
            override fun onPdfRenderSuccess() {
                log("onPdfRenderSuccess")
            }
        },
        zoomListener = object : PdfRendererView.ZoomListener {
            override fun onZoomChanged(isZoomedIn: Boolean, scale: Float) {
                log("onZoomChanged=$isZoomedIn, scale=$scale")
            }
        }
    )

//    if (viewModel.isLoadingFile.value) {
//        Box(Modifier.fillMaxSize(), Alignment.Center) {
//            // 显示加载进度
//            CircularProgressIndicator(
//                modifier = Modifier.width(32.dp),
//                color = MaterialTheme.colorScheme.secondary,
//                trackColor = MaterialTheme.colorScheme.surfaceVariant
//            )
//        }
//    }
}

/**
 * 获取当前 Compose 界面的 Activity 窗口（Window）
 */
@Composable
fun getCurrentWindow(): Window? {
    val context = LocalContext.current
    return if (context is Activity) {
        context.window
    } else {
        null // 宿主不是 Activity 时返回 null（如 Dialog）
    }
}

//import com.github.barteksc.pdfviewer.PDFView
//@Composable
//fun PDFViewWrapper(viewModel: MyViewModel, currentBook: Book){
//    val context = LocalContext.current
//    DisposableEffect(currentBook) {
//        viewModel.pdfView.value = PDFView(context, null).apply {
//            myInit(currentBook, viewModel).loadPages()
//        }
//
//        onDispose {
//            // viewModel.pdfView.value?.destroy() // 销毁 WebView 释放资源
//            viewModel.pdfView.value = null
//        }
//    }
//    if (viewModel.pdfView.value != null) {
//        // 将 PDFView 添加到 Composition
//        AndroidView(
//            factory = { viewModel.pdfView.value!! }
//        )
//    }
//}
//fun PDFView.myInit(currentBook: Book, viewModel: MyViewModel): PDFView {
//    //.pages(0, 2, 1, 3, 3, 3) // all pages are displayed by default
//    fromUri(currentBook.uri)
//        .defaultPage(currentBook.page)
//        .enableSwipe(true) // allows to block changing pages using swipe
//        .enableDoubletap(true)
//        .swipeHorizontal(currentBook.landscape == 0) // 横屏垂直滚动，竖屏水平滚动？
//        //.scrollHandle(DefaultScrollHandle(ctx)) // eg. 自定义滚动手柄，用于显示阅读进度
//        .pageFitPolicy(FitPolicy.WIDTH) // mode to fit pages in the view
//        .fitEachPage(false) // fit each page to the view, else smaller pages are scaled relative to largest page.
//        .pageSnap(false) // snap pages to screen boundaries
//        //.pageFling(false) // make a fling change only a single page like ViewPager
//        .nightMode(false) // toggle night mode
//        .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
//        .enableAntialiasing(true) // improve rendering a little bit on low-res screens
//        .spacing(0)// spacing between pages in dp. To define spacing color, set view background
//        .autoSpacing(false) // add dynamic spacing to fit each page on its own on the screen
//        .password(null)
//        // 加载完成后配置called after document is loaded and starts to be rendered
//        .onLoad { nbPages ->
//            viewModel.isLoadingFile.value = false
//            viewModel.updateTotalPages(nbPages)
//            zoomTo(currentBook.zoom)
//            jumpTo(currentBook.page)
//
//            // 延迟恢复滚动位置（等待布局完成）
//            postDelayed({
//                scrollTo(currentBook.scrollX, currentBook.scrollY)
//            }, 300)
//        }
//        .onRender {
//            // 渲染完成监听  called after document is rendered for the first time
//        }
//        .onPageScroll { page, position ->
//            // 滚动监听
//            viewModel.updatePage(page)
//            viewModel.updateScroll(position.toInt(), position.toInt())
//        }
//        .onPageChange { page, _ ->
//            // 页面变化监听
//            viewModel.updatePage(page)
//        }
//    // called on single tap, return true if handled, false to toggle scroll handle visibility
//       .onTap{
//           viewModel.isFullScreen.value = !viewModel.isFullScreen.value
//           true
//       }
////                    // allows to draw something on the current page, usually visible in the middle of the screen
////                    .onDraw(onDrawListener)
////                    // allows to draw something on all pages, separately for every page. Called only for visible pages
////                    .onDrawAll(onDrawListener)
////
////                    .onError(onErrorListener)
////                    .onPageError(onPageErrorListener)
////
//
////                    .onLongPress(onLongPressListener)
////                    .linkHandler(DefaultLinkHandler)
//
//    return this
//}

