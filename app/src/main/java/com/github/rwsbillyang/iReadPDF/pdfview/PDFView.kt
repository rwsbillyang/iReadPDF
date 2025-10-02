package com.github.rwsbillyang.iReadPDF.pdfview

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.github.rwsbillyang.iReadPDF.LocalViewModel
import com.github.rwsbillyang.iReadPDF.MyViewModel
import kotlinx.coroutines.launch

internal const val TAG = "PDFView"



@Composable
fun PdfView(
    source: PdfSource,
    modifier: Modifier = Modifier,
    cacheStrategy: CacheStrategy = CacheStrategy.MAXIMIZE_PERFORMANCE,
    page: Int = 0,
    statusCallBack: StatusCallBack? = null,
    zoomListener: ZoomListener? = null,
    onReady: (() -> Unit)? = null
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val viewModel: MyViewModel = LocalViewModel.current
    // lifecycleOwner: LifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current,
    val configuration = LocalConfiguration.current
    Log.d(TAG, "screenWidthDp=${configuration.screenWidthDp}, screenHeightDp=${configuration.screenHeightDp}")


    val listState = rememberLazyListState()
    var err by remember { mutableStateOf("PDF loading ") }

//    LaunchedEffect(listState) {
//        snapshotFlow { listState.firstVisibleItemIndex }//listState.firstVisibleItemIndex
//            .map { index -> index > 0 }
//            .distinctUntilChanged()
//            .filter { it }
//            .collect {
//               // MyAnalyticsService.sendScrolledPastFirstItemEvent()
//                Log.d(TAG, "listState.layoutInfo.visibleItemsInfo.size=${listState.layoutInfo.visibleItemsInfo.size}")
//            }
//    }


    DisposableEffect(source) {
        scope.launch {
            val name = source.getDisplayName(ctx)
            val id = source.getFileId(ctx)
            statusCallBack?.onPdfLoadStart(name, id)
            val fd = source.getFileDescriptor(ctx)
            val fileId = source.getFileId(ctx)
            if (fd != null || fileId != null) {
                val pdfPageLoader = PdfPageLoader.create(fd!!, fileId!!, ctx, cacheStrategy)
                viewModel.pdfPageLoader.value = pdfPageLoader

                val w = configuration.screenWidthDp
                val h = (w * pdfPageLoader.pageSize.height) / pdfPageLoader.pageSize.width
                viewModel.lazyItemWidth.value = w
                viewModel.lazyItemHeight.value =  h
                Log.d(TAG, "lazy item: w=$w, h=$h")
                statusCallBack?.onPdfLoadSuccess(name, id)
                listState.scrollToItem(index = page, 0)
            } else {
                err = "Load PDF fail: fail to get fileDescriptor or id"
                statusCallBack?.onError(err!!)
            }
        }

        onDispose {
            Log.d(TAG, "onDispose: release PdfRenderer and fd")
            viewModel.pdfPageLoader.value?.closePdfRender()
            viewModel.pdfPageLoader.value = null
        }
    }



    //quality low:5 , medium: 3, high:1
    //val qualityRatio: Int = ctx.resources.displayMetrics.densityDpi / (1 * 72)

    if (viewModel.pdfPageLoader.value == null) {
        Column(modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(err)
        }
    } else {
        Box(modifier) {
            LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(viewModel.pdfPageLoader.value!!.pdfRenderer.pageCount, { it.toString() }) { index ->
                    Box(modifier = Modifier.width(viewModel.lazyItemWidth.value.dp).height(viewModel.lazyItemHeight.value.dp)){
                        PdfPage(index)
                    }
                }
            }

            //TODO Operation tool bar

        }
    }
}


@Composable
fun PdfPage(page: Int) {
    var loading by remember { mutableStateOf(true) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val viewModel: MyViewModel = LocalViewModel.current
    val pdfPageLoader = viewModel.pdfPageLoader.value!!

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
                //ContentScale.Crop //在较小的手机屏幕上。因pdf页面较大，导致外围不显示，只是显示中间部分
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
