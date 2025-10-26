package com.github.rwsbillyang.iReadPDF.pdfview

import android.content.res.Configuration
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.github.rwsbillyang.iReadPDF.db.Book
import com.github.rwsbillyang.iReadPDF.db.PdfQuality
import com.github.rwsbillyang.iReadPDF.log
import kotlinx.coroutines.flow.distinctUntilChanged

internal const val TAG = "MyApp"




@Composable
fun PdfView(
    pdfPageLoader: PdfPageLoader,
    book: Book,
    modifier: Modifier = Modifier,
    statusCallBack: StatusCallBack,
    disableMovePdf: Boolean
) {
    val configuration = LocalConfiguration.current

    //对于文字版pdf，黑色模式下其背景色为黑，文字也为黑，故对其bitmap像素进行取反操作，从而文字颜色变白，可以正常阅读
    // 而图片扫描格式的PDF，可正常显示，无需进行位取反，禁用黑色模式，避免位像素进行取反运算
    //对于纸质扫描图片格式的pdf，禁用黑色模式
    val darkThemeEnabled = if(book.disableDarkMode == 1) false else Configuration.UI_MODE_NIGHT_YES == configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)
    val quality by remember{ mutableStateOf(
        when(PdfQuality.valueOf(book.quality)){
            PdfQuality.Low -> 1.0f
            PdfQuality.Middle -> configuration.densityDpi / (2*72.0f)
            PdfQuality.High -> configuration.densityDpi / 72.0f
        }
    ) }

    //listen page change
    val listState = rememberLazyListState(book.page, 0)
    LaunchedEffect(book.page) {
        Log.d(TAG, "requestScrollToItem ${book.page}, pageOffset=${ book.pageOffset}")
        listState.requestScrollToItem(book.page, book.pageOffset)
        statusCallBack.onTotalPages(pdfPageLoader.pageCount)
        snapshotFlow { listState.firstVisibleItemScrollOffset }
            .distinctUntilChanged()
            .collect {
                //Log.d(TAG, "firstVisibleItemIndex=${listState.firstVisibleItemIndex}, listState.firstVisibleItemScrollOffset=${listState.firstVisibleItemScrollOffset}")
                statusCallBack.onPageChanged(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset)
            }
    }

    val lazyItemWidth = remember(book.rotation) {mutableStateOf(if(book.rotation == 0) configuration.screenWidthDp else configuration.screenHeightDp)}
    val lazyItemHeight = remember(lazyItemWidth.value, pdfPageLoader.pageSize.width, pdfPageLoader.pageSize.height) {
        mutableStateOf((lazyItemWidth.value * pdfPageLoader.pageSize.height) / pdfPageLoader.pageSize.width)
    }
    Log.d(TAG, "PdfView: lazyItemWidth=${lazyItemWidth.value}, lazyItemHeight=${lazyItemHeight.value}")

    var scale by remember { mutableFloatStateOf(book.zoom) }

    //只要禁止了move，就将其offset设为0，避免偏移
    var offset by remember(disableMovePdf) { mutableStateOf(if(disableMovePdf) Offset(0f, 0f) else Offset(book.offsetX, book.offsetY))}

    //val disableModePdfRef = rememberUpdatedState(disableMovePdf)
    val transformableState  = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        val newOffset = if(book.rotation == 90 || book.rotation == -90 || book.rotation == 270){
            Offset(0f, offsetChange.x) //only support "horizontal" move  //Offset(-offsetChange.y, offsetChange.x)
        }else  Offset(offsetChange.x, 0f) //only support horizontal move //offsetChange

        if(!disableMovePdf)
            offset += newOffset

        //若disableMovePdf，最终newOffset并没有记录到book的offsetX offsetY中
        statusCallBack.onTransformStateChanged(zoomChange, newOffset.x, newOffset.y, rotationChange)

        log("zoomChange=$zoomChange, offsetChange=$offsetChange, rotationChange=$rotationChange")
    }

    LazyColumn(modifier.fillMaxSize().graphicsLayer(
        scaleX = scale,
        scaleY = scale,
        rotationZ = book.rotation.toFloat(),
        translationX = if(disableMovePdf) 0f else offset.x,
        translationY = if(disableMovePdf) 0f else offset.y).background(MaterialTheme.colorScheme.surface)
        .transformable(state = transformableState)
        , listState, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        items(pdfPageLoader.pageCount, { it.toString() }) { index ->
            Box(modifier = Modifier.width(lazyItemWidth.value.dp).height(lazyItemHeight.value.dp)){
                PdfPage(pdfPageLoader, index, quality, darkThemeEnabled)
            }
        }
    }
}


@Composable
fun PdfPage(pdfPageLoader:PdfPageLoader, page: Int, quality: Float, darkThemeEnabled: Boolean) {
    var loading by remember { mutableStateOf(true) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }


    LaunchedEffect(pdfPageLoader, page) {
        //load page 若缓存中有，从缓存中取出，否则渲染，并加入缓存
        val cachedBitmap = pdfPageLoader.loadPage(page,  quality, darkThemeEnabled)
        //Log.d(TAG, "load page $page done!")
        if (cachedBitmap != null) {
            bitmap = cachedBitmap
            loading = false
        }
    }


    if (loading) {
        Row(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterVertically)
        {
            CircularProgressIndicator() // 圆形进度条
            Text("loading page $page ")
        }
    } else {
        bitmap?.let {
            Image(
                it.asImageBitmap(), "Pdf page $page",
                Modifier.fillMaxSize(), Alignment.Center,
                ContentScale.Fit, //保持横宽比拉伸pdf
                //ContentScale.Crop //在较小的手机屏幕上。因pdf页面较大，导致外围不显示，只是显示bitmap的中间部分
                //ContentScale.FillBounds // 对bitmap进行拉伸填充屏幕，会变形
            ) //ContentScale.Fit ContentScale.FitWidth ContentScale.FitHeight
        }?:Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally)
        {
            Text("something wrong")
        }
    }
}
