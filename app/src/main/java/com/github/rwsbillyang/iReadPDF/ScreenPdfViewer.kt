package com.github.rwsbillyang.iReadPDF

import android.app.Activity
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.rounded.Brightness1
import androidx.compose.material.icons.rounded.Brightness4
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.FullscreenExit
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.TextRotateUp
import androidx.compose.material.icons.rounded.TextRotationDown
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import com.github.rwsbillyang.composerouter.ScreenCall
import com.github.rwsbillyang.composerouter.useRouter
import com.github.rwsbillyang.iReadPDF.AppConstants.TAG
import com.github.rwsbillyang.iReadPDF.db.Book
import com.github.rwsbillyang.iReadPDF.pdfview.PdfView
import com.github.rwsbillyang.iReadPDF.pdfview.StatusCallBack
import com.github.rwsbillyang.iReadPDF.pdfview.setFullScreen
import com.github.rwsbillyang.iReadPDF.pdfview.setLandscape


@Composable
fun ToolBarItem(strId: Int, icon: ImageVector, modifier: Modifier, onClick:()->Unit){
    val text: String = stringResource(strId)
    Column(modifier.fillMaxHeight().wrapContentWidth(Alignment.CenterHorizontally)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onClick() })
            },
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        Icon(icon, text, Modifier.fillMaxWidth().size(32.dp), MaterialTheme.colorScheme.onSurfaceVariant)

        //MaterialTheme.colorScheme.secondaryContainer: 在黑色模式下，期望字体在白色背景中呈现黑色，结果字体仍是白色，但淡色模式下期望白色正常
        //TODO：darkmode 黑白色设置无效，总被系统修改，即使采用如下，不管Black还是White，黑色模式下总是白色
//        val configuration = LocalConfiguration.current
//        val darkThemeEnabled = Configuration.UI_MODE_NIGHT_YES == configuration.uiMode.and(
//            Configuration.UI_MODE_NIGHT_MASK)
//        val color = if(darkThemeEnabled) Color.Black else Color.White
//        Log.d(TAG, "darkThemeEnabled=$darkThemeEnabled, color=${color}")

        //按钮底部文字
        Text(text, Modifier.fillMaxWidth(), MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp,
            maxLines= 2, minLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelSmall)
    }
}
@Composable
fun ToolsBar(showPageNumberInputDlg: (show: Boolean)->Unit ,hideToolBar: ()-> Unit){
    val viewModel: MyViewModel = LocalViewModel.current
    val b = viewModel.currentBook
    if(b == null)
    {
        log("to show tools bar: no book yet, ignore")
        return
    }
    val router = useRouter()
    //val context = LocalContext.current
    val window = getCurrentWindow()

    //zIndex(1f)大者在小者之上
    Row(Modifier.fillMaxWidth().height(96.dp).graphicsLayer(
        rotationZ = b.rotation.toFloat()//toolbar跟随旋转
    ).background(MaterialTheme.colorScheme.surfaceVariant.copy(0.9f)).zIndex(1f),
        Arrangement.SpaceAround, Alignment.Bottom){
        val w = Modifier.weight(1f)

        if(b.disableDarkMode != 0){
            ToolBarItem(R.string.enable_dark_mode, Icons.Rounded.Brightness4, w){
                b.disableDarkMode = 0
                hideToolBar()
            }
        }else{
            ToolBarItem(R.string.disable_dark_mode, Icons.Rounded.Brightness1, w){
                b.disableDarkMode = 1
                hideToolBar()
            }
        }

        ToolBarItem(R.string.jump, Icons.Rounded.OpenInNew, w){
            showPageNumberInputDlg(true)
            hideToolBar()
        }

//        if(b.landscape == 1){
//            ToolBarItem(R.string.portrait, Icons.Rounded.MobileScreenShare, w){
//                b.landscape = 0
//                (context as? Activity)?.setLandscape(0)
//                hideToolBar()
//            }
//        }else{
//            ToolBarItem(R.string.landscape, Icons.Rounded.ScreenRotation, w){
//                //b.landscape = 1
//                (context as? Activity)?.setLandscape(1)
//                hideToolBar()
//            }
//        }

        if(b.rotation != 0){
            ToolBarItem(R.string.rotation0, Icons.Rounded.TextRotateUp, w){
                b.rotation = 0
                hideToolBar()
            }
        }else{
            ToolBarItem(R.string.rotation90, Icons.Rounded.TextRotationDown, w){
                b.rotation = 90
                hideToolBar()
            }
        }

        if(window != null){
            if(b.fullScreen == 1){
                ToolBarItem(R.string.fullscreen_exit, Icons.Rounded.FullscreenExit, w){
                    b.fullScreen = 0
                    window.setFullScreen(false)
                    hideToolBar()
                }
            }else{
                ToolBarItem(R.string.fullscreen, Icons.Rounded.Fullscreen, w){
                    b.fullScreen = 1
                    window.setFullScreen(true)
                    hideToolBar()
                }
            }
        }


        ToolBarItem(R.string.bookshelf, Icons.AutoMirrored.Filled.LibraryBooks, w){
            router.navByName(AppConstants.AppRoutes.BookShelf)
        }

        ToolBarItem(R.string.settings, Icons.Rounded.Settings, w){
            //router.navByName(AppConstants.AppRoutes.BookSettings, b)
            router.navByName(AppConstants.AppRoutes.Settings)
        }
    }
}



@Composable
fun ScreenPdfViewer(call: ScreenCall) {
    val book: Book? = call.props as? Book
    if(book == null){
        Column(Modifier.fillMaxSize(),verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
            Text("no book to open")
        }
        return
    }

    val ctx = LocalContext.current
    val viewModel: MyViewModel = LocalViewModel.current

    var showToolsBar by remember { mutableStateOf(false) }

    var showInputPageNumber by remember { mutableStateOf(false) }
    var pageNumber by remember { mutableStateOf<Int?>(null) }


    val window = getCurrentWindow()

    LaunchedEffect(book){
        viewModel.updateCurrentBook(book, ctx)

        window?.setFullScreen(book.fullScreen == 1)
        (ctx as? Activity)?.setLandscape(book.landscape)
    }

    if(viewModel.pdfPageLoader == null){
        Column(Modifier.fillMaxSize().padding(call.scaffoldPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (viewModel.loadingPdf)
                CircularProgressIndicator()
            else
                Text("Load PDF fail: no file")
        }
    }else{
        val statusCallBack = object : StatusCallBack {
            override fun onTotalPages(total: Int) {
                book.total = total
            }
            override fun onPageChanged(currentPage: Int, pageOffset: Int) {
                //log("onPageChanged: currentPage=$currentPage, pageOffset=$pageOffset")
                book.page = currentPage
                book.pageOffset = pageOffset
            }
            override fun onTransformStateChanged(zoomChange: Float, moveX: Float, moveY: Float, rotationChange: Float) {
                viewModel.updateTransformState(zoomChange, moveX, moveY)
            }
        }


        Box(modifier = Modifier.fillMaxSize().padding(call.scaffoldPadding)
            .pointerInput(Unit) { detectTapGestures(onTap = { showToolsBar = !showToolsBar }) }
            .layout { measurable, constraints ->
                //若旋转90度，对宽高进行置换
                val newConstraints =
                    if (book.rotation == 90 || book.rotation == -90 || book.rotation == 270) {
                        constraints.copy(
                            minWidth = constraints.minHeight,
                            maxWidth = constraints.maxHeight,
                            minHeight = constraints.minWidth,
                            maxHeight = constraints.maxWidth
                        )
                    } else {
                        constraints
                    }

                //measurable代表着"具备可测量能力的"，即当前的box，经过measure之后，得到一个"具备可置放能力的"placeable
                val placeable = measurable.measure(newConstraints)
                //log("layout: rotation=${book.rotation},constraints:minWidth=${constraints.minWidth}, maxWidth=${constraints.maxWidth}, minHeight=${constraints.minHeight}, maxHeight=${constraints.maxHeight}, placeable.width=${placeable.width}, placeable.height=${placeable.height}")

                //根据placeable中的置放数据，进行layout后，返回MeasureResult对组件进行重新布局layout
                layout(placeable.width, placeable.height) { placeable.placeRelative(0, 0) }
            }

            ,Alignment.Center //若将Toolbar放在底部，全屏后无法正确显示出来（可能布局变化，导致在底部屏幕之外）
        )
        {
            PdfView(
                viewModel.pdfPageLoader!!,
                book,
                Modifier.fillMaxSize().zIndex(0f),
                statusCallBack,
                viewModel.disableMovePdf
            )

            if(showToolsBar){
                ToolsBar({showInputPageNumber = it}){showToolsBar = false}
            }
            if(showInputPageNumber){
                InputDialog(book.rotation, stringResource(id = R.string.page_number), "0~${book.total}", KeyboardType.Number, onCancel = {showInputPageNumber = false}){
                    showInputPageNumber = false
                    log("got page number $it")
                    if(!it.isNullOrEmpty()){
                        try{
                            val n = it.toInt()
                            if (n >= 0 && n < viewModel.pdfPageLoader!!.pageCount) {
                                book.page = n
                                book.pageOffset = 0
                                if (book.rotation == 90 || book.rotation == -90 || book.rotation == 270)
                                    book.offsetX = 0f
                                else
                                    book.offsetY = 0f
                                pageNumber = n
                            }
                        }catch (e: NumberFormatException ){
                            Log.w(TAG, "invalid number $it")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InputDialog(rotation:Int, title: String?, placeholder: String, keyboardType: KeyboardType, initial: String? = null, onCancel: () -> Unit, onOK: (result: String?) -> Unit){
    MyDialog<String, String>(rotation, title, stringResource(id = R.string.cancel), stringResource(id = R.string.ok),
        initial, onOK, onCancel){v, notifyResult->
        //notifyResult即Dialog中的{result.value = it}，将结果赋值给Dialog中的result

        var text by remember { mutableStateOf(v?:"") }

        TextField(
            value = text,
            placeholder = {Text(placeholder, color= MaterialTheme.colorScheme.secondary)},
            onValueChange = { text = it; notifyResult(text) },
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {onOK(text)}),
            singleLine = true
        )
    }
}
@Composable
fun <T, R> MyDialog(
    rotation:Int,
    title: String? = null,
    cancelText: String = "Cancel",
    okText: String = "OK",
    value: T? = null,
    onOK: (result: R?) -> Unit,
    onCancel: () -> Unit,
    content: @Composable (value: T?, notifyResult: (r: R?)->Unit)->Unit
) {
    val result = remember { mutableStateOf<R?>(null) }
    val currentResult = rememberUpdatedState(result.value)
    Dialog(onDismissRequest = { onCancel() }) {
        Card(Modifier.fillMaxWidth().height(250.dp).graphicsLayer(
            rotationZ = rotation.toFloat()//跟随旋转
        ).padding(16.dp),
            shape = RoundedCornerShape(10.dp),
        ){
            Column(Modifier.fillMaxSize(), Arrangement.SpaceAround, Alignment.CenterHorizontally)
            {
                title?.let{ Text(it, Modifier.padding(16.dp)) }
                content(value){result.value = it} //即value 和 {result.value = it}，作为两个实参传递给InputNumberDialog中的v, notifyResult->
                Row(Modifier.fillMaxWidth(),Arrangement.SpaceEvenly) {
                    OutlinedButton(
                        onClick = { onCancel() },
                        modifier = Modifier.width(100.dp),
                    ) {
                        Text(cancelText)
                    }
                    OutlinedButton(
                        onClick = { onOK(currentResult.value) },
                        modifier = Modifier.width(100.dp),
                    ) {
                        Text(okText)
                    }
                }
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
//fun PDFViewWrapper(viewModel: MyViewModel, book: Book){
//    val context = LocalContext.current
//    DisposableEffect(book) {
//        viewModel.pdfView.value = PDFView(context, null).apply {
//            myInit(book, viewModel).loadPages()
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
//fun PDFView.myInit(book: Book, viewModel: MyViewModel): PDFView {
//    //.pages(0, 2, 1, 3, 3, 3) // all pages are displayed by default
//    fromUri(book.uri)
//        .defaultPage(book.page)
//        .enableSwipe(true) // allows to block changing pages using swipe
//        .enableDoubletap(true)
//        .swipeHorizontal(book.landscape == 0) // 横屏垂直滚动，竖屏水平滚动？
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
//            zoomTo(book.zoom)
//            jumpTo(book.page)
//
//            // 延迟恢复滚动位置（等待布局完成）
//            postDelayed({
//                scrollTo(book.scrollX, book.scrollY)
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

