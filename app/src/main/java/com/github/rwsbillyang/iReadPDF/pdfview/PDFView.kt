package com.github.rwsbillyang.iReadPDF.pdfview

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

internal const val TAG = "PDFView"




@Composable
fun PdfView(
    pdfPageLoader: PdfPageLoader,
    page: Int = 0,
    initialZoom: Float = 1.0f,
    initialOffsetX: Float = 0.0f,
    initialOffsetY: Float = 0.0f,
    modifier: Modifier = Modifier,
    statusCallBack: StatusCallBack? = null
) {
 //   val ctx = LocalContext.current
 //   val scope = rememberCoroutineScope()
   // val viewModel: MyViewModel = LocalViewModel.current
    // lifecycleOwner: LifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current,

    val configuration = LocalConfiguration.current
    Log.d(TAG, "PdfView: screenWidthDp=${configuration.screenWidthDp}, screenHeightDp=${configuration.screenHeightDp}")


    //val rotation = mutableFloatStateOf(0f)
    var scale by remember { mutableFloatStateOf(initialZoom)}
    var offset by remember { mutableStateOf(Offset(initialOffsetX, initialOffsetY))}
    val transformableState  = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        offset += offsetChange
        statusCallBack?.onTransformStateChanged(zoomChange, offsetChange, rotationChange) ?: Log.w(TAG, "not save zoomChange=$zoomChange, offsetChange=$offsetChange, rotationChange=$rotationChange")
    }

    //var pdfPageLoader by remember { mutableStateOf<PdfPageLoader?>(null)}
    val lazyItemWidth = remember { mutableStateOf(configuration.screenWidthDp) }
    val lazyItemHeight = remember(pdfPageLoader.pageSize) {
        mutableStateOf((lazyItemWidth.value * pdfPageLoader.pageSize.height) / pdfPageLoader.pageSize.width)
    }

    //listen page change
    val listState = rememberLazyListState()
//    LaunchedEffect(listState) {
//        snapshotFlow { listState.firstVisibleItemIndex }
//            .map { it >= 0 }
//            .distinctUntilChanged()
//            .collect {
//
//               // MyAnalyticsService.sendScrolledPastFirstItemEvent()
//                Log.d(TAG, "listState.layoutInfo.visibleItemsInfo.size=${listState.layoutInfo.visibleItemsInfo.size}")
//            }
//    }



    Box(modifier.graphicsLayer(
        scaleX = scale, //viewModel.currentBook.value?.zoom?:1.0f,
        scaleY = scale, //viewModel.currentBook.value?.zoom?:1.0f,
        //rotationZ = viewModel.rotation.value,
        translationX = offset.x, //viewModel.currentBook.value?.offsetX?:0f,
        translationY = offset.y,//viewModel.currentBook.value?.offsetX?:0f,
    ).transformable(state = transformableState)) {
        LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items(pdfPageLoader.pageCount, { it.toString() }) { index ->
                Box(modifier = Modifier.width(lazyItemWidth.value.dp).height(lazyItemHeight.value.dp)){
                    PdfPage(pdfPageLoader, index)
                }
            }
        }
    }
}


@Composable
fun PdfPage(pdfPageLoader:PdfPageLoader , page: Int) {
    var loading by remember { mutableStateOf(true) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    //Log.d(TAG, "PdfPage $page")

    LaunchedEffect(pdfPageLoader, page) {
        //load page 若缓存中有，从缓存中取出，否则渲染，并加入缓存
        val cachedBitmap = pdfPageLoader.loadPage(page, 1.0F)
        //Log.d(TAG, "load page $page done!")
        if (cachedBitmap != null) {
            bitmap = cachedBitmap
            loading = false
        }
    }


    if (loading) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator() // 圆形进度条
            Text("loading page $page ")
        }
    } else {
        bitmap?.let {
            Image(
                it.asImageBitmap(), "Pdf page $page",
                Modifier.fillMaxSize().background(Color.Red), Alignment.Center,
                ContentScale.Fit, //保持横宽比拉伸pdf
                //ContentScale.Crop //在较小的手机屏幕上。因pdf页面较大，导致外围不显示，只是显示bitmap的中间部分
                //ContentScale.FillBounds // 对bitmap进行拉伸填充屏幕，会变形
            ) //ContentScale.Fit ContentScale.FitWidth ContentScale.FitHeight
        }?:Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("something wrong")
        }
    }
}
