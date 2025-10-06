package com.github.rwsbillyang.iReadPDF

import android.app.Activity
import android.content.pm.ActivityInfo
import android.util.Log
import android.view.Window
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.rounded.Adjust
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.FullscreenExit
import androidx.compose.material.icons.rounded.Landscape
import androidx.compose.material.icons.rounded.Portrait
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.github.rwsbillyang.composerouter.ScreenCall
import com.github.rwsbillyang.composerouter.useRouter
import com.github.rwsbillyang.iReadPDF.AppConstants.TAG
import com.github.rwsbillyang.iReadPDF.db.Book
import com.github.rwsbillyang.iReadPDF.pdfview.PdfView
import com.github.rwsbillyang.iReadPDF.pdfview.StatusCallBack

@Composable
fun ToolBarItem(strId: Int, icon: ImageVector, modifier: Modifier, onClick:()->Unit){
    val text: String = stringResource(strId)
    Column(
        modifier
            .fillMaxHeight()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onClick() })
            },
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        Icon(icon, text, Modifier.fillMaxWidth().size(32.dp), MaterialTheme.colorScheme.secondaryContainer)


        //MaterialTheme.colorScheme.secondaryContainer: 在黑色模式下，期望字体在白色背景中呈现黑色，结果字体仍是白色，但淡色模式下期望白色正常
        //TODO：黑白色设置无效，总被系统修改，即使采用如下，不管Black还是White，黑色模式下总是白色
//        val configuration = LocalConfiguration.current
//        val darkThemeEnabled = Configuration.UI_MODE_NIGHT_YES == configuration.uiMode.and(
//            Configuration.UI_MODE_NIGHT_MASK)
//        val color = if(darkThemeEnabled) Color.Black else Color.White
//        Log.d(TAG, "darkThemeEnabled=$darkThemeEnabled, color=${color}")

        //按钮底部文字
        Text(text, Modifier.fillMaxWidth(), MaterialTheme.colorScheme.secondaryContainer,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp,
            maxLines= 2, minLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodySmall)
    }
}
@Composable
fun ToolsBar(hideToolBar: ()-> Unit){
    val viewModel: MyViewModel = LocalViewModel.current
    log("to show tools bar")
    //val b = viewModel.currentBook
    val router = useRouter()
    val context = LocalContext.current
    val window = getCurrentWindow()

    //zIndex(1f)大者在小者之上
    Row(
        Modifier
            .fillMaxWidth()
            .height(96.dp)
            .background(MaterialTheme.colorScheme.onSecondaryContainer.copy(0.8f))
            .zIndex(1f),
        Arrangement.SpaceAround, Alignment.Bottom){
        val w = Modifier.weight(1f)


        ToolBarItem(R.string.jump, Icons.Rounded.Adjust, w){
            Log.d(TAG, "TODO: jump to page")
            hideToolBar()
        }

        if(viewModel.currentBook?.landscape == 1){
            ToolBarItem(R.string.portrait, Icons.Rounded.Portrait, w){
                (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                viewModel.currentBook?.landscape = 0
                hideToolBar()
            }
        }else{
            ToolBarItem(R.string.landscape, Icons.Rounded.Landscape, w){
                (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                viewModel.currentBook?.landscape = 1
                hideToolBar()
            }
        }
        if(window != null){
            if(viewModel.currentBook?.fullScreen == 1){
                ToolBarItem(R.string.fullscreen_exit, Icons.Rounded.FullscreenExit, w){
                    window.setFullScreen(false)
                    viewModel.currentBook?.fullScreen = 0
                    hideToolBar()
                }
            }else{
                ToolBarItem(R.string.fullscreen, Icons.Rounded.Fullscreen, w){
                    window.setFullScreen(true)
                    viewModel.currentBook?.fullScreen = 1
                    hideToolBar()
                }
            }
        }


        ToolBarItem(R.string.bookshelf, Icons.AutoMirrored.Filled.MenuBook, w){
            router.navByName(AppRoutes.BookShelf)
        }

        ToolBarItem(R.string.settings, Icons.Rounded.Settings, w){
            router.navByName(AppRoutes.Settings)
        }
    }
}

fun Window.setFullScreen(enabled: Boolean){
    if(enabled){
        // 进入全屏：隐藏系统栏（状态栏+导航栏）
        WindowCompat.setDecorFitsSystemWindows(this, false) // 让内容延伸到系统栏区域
        WindowCompat.getInsetsController(this, this.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars()) // 隐藏系统栏
            //systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE // 滑动边缘唤出系统栏
        }
    }else{
        // 退出全屏：显示系统栏
        WindowCompat.setDecorFitsSystemWindows(this, true) // 内容不延伸到系统栏
        WindowCompat.getInsetsController(this, this.decorView).apply {
            show(WindowInsetsCompat.Type.systemBars()) // 显示系统栏
        }
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

    val ctx = LocalContext.current
    val viewModel: MyViewModel = LocalViewModel.current

    var showToolsBar by remember { mutableStateOf(false) }
    val window = getCurrentWindow()

    LaunchedEffect(currentBook){
        viewModel.onBookMaybeChanged(currentBook, ctx)
        window?.setFullScreen(currentBook.fullScreen == 1)
    }

    if(viewModel.pdfPageLoader == null){
        Column(
            Modifier
                .fillMaxSize()
                .padding(call.scaffoldPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (viewModel.loadingPdf)
                CircularProgressIndicator()
            else
                Text("Load PDF fail: no file")
        }
    }else{
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(call.scaffoldPadding)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { showToolsBar = !showToolsBar })
            }, Alignment.Center //若将Toolbar放在底部，全屏后无法正确显示出来（可能布局变化，导致在底部屏幕之外）
            )
        {
            PdfView(
                viewModel.pdfPageLoader!!,
                currentBook,
                Modifier
                    .fillMaxSize()
                    .zIndex(0f),
                object : StatusCallBack {
                    override fun onPageChanged(currentPage: Int, pageOffset: Int) {
                        //log("onPageChanged: currentPage=$currentPage, pageOffset=$pageOffset")
                        currentBook.page = currentPage
                        currentBook.pageOffset = pageOffset
                    }

                    override fun onTransformStateChanged(
                        zoomChange: Float,
                        offsetChange: Offset,
                        rotationChange: Float
                    ) {

                        //viewModel.rotation.value += rotationChange
                        viewModel.updateTransformState(zoomChange, offsetChange.x, offsetChange.y)
                    }
                }
            )

            if(showToolsBar){
                ToolsBar{showToolsBar = false}
            }
        }
    }
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

