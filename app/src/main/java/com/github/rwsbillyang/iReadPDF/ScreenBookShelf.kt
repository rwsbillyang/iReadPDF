package com.github.rwsbillyang.iReadPDF

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Brightness1
import androidx.compose.material.icons.rounded.Brightness4
import androidx.compose.material.icons.rounded.PostAdd
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.LensBlur
import androidx.compose.material.icons.rounded.PhotoSizeSelectLarge
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.github.rwsbillyang.composerouter.ScreenCall
import com.github.rwsbillyang.composerouter.useRouter
import com.github.rwsbillyang.iReadPDF.db.Book
import com.github.rwsbillyang.iReadPDF.db.MyDao
import com.github.rwsbillyang.iReadPDF.db.PdfQuality
import com.github.rwsbillyang.iReadPDF.pdfview.CacheManager
import com.github.rwsbillyang.iReadPDF.pdfview.FileUtil
import com.github.rwsbillyang.iReadPDF.pdfview.PdfPageLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

//导航栏按钮工具栏
@Composable
fun BookShelfToolIcons(){
    val ctx =  LocalContext.current
    val viewModel: MyViewModel = LocalViewModel.current
    val scope = rememberCoroutineScope()
    val dao = LocalDao.current
    val router = useRouter()

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments(), // OpenDocument()
        onResult = { uriList ->
            scope.launch {
                withContext(Dispatchers.IO){
                    uriList.forEach { handleSelectedPdfUri(ctx, dao,viewModel, it) }
                }
            }
        } // 处理选中的 Uri
    )
    Spacer(Modifier.width(8.dp))
    IconButton(onClick = {
        filePickerLauncher.launch(arrayOf("application/pdf") )
    }) {
        Icon(Icons.Rounded.PostAdd, contentDescription = "add books")
        //Text("Add Books")
    }

    IconButton(onClick = {
        viewModel.isEditingShelf.value = !viewModel.isEditingShelf.value
    }) {
        Icon(Icons.Rounded.Edit, contentDescription = "Manage")
        //Text("Manage")
    }
    IconButton(onClick = { router.navByName(AppConstants.AppRoutes.Settings) }) {
        Icon(Icons.Rounded.Settings, contentDescription = "Settings")
    }
}

// 处理选中的 PDF Uri（包含 MD5 计算和缓存）
suspend fun handleSelectedPdfUri(ctx: Context, dao: MyDao, viewModel: MyViewModel, uri: Uri) {
    FileUtil.calculateMd5(ctx, uri)?.let {
        val b = dao.findOne(it)
        val originalFileName = FileUtil.getFileNameFromUri(ctx, uri) ?: b?.name?: "unknown"
        //去掉文件名称后面的.pdf扩展名（不分大小写），同时文件名称中保留原始大小写
        val name = if(originalFileName.substringAfterLast('.').lowercase() == "pdf")originalFileName.substringBeforeLast('.') else originalFileName
        val newBook = Book(it, name)
        if(b == null){
            //TODO：copy一份，否则以后通过该uri加载，没有权限
            FileUtil.copyFromUri(ctx, uri, CacheManager.defaultPdfFile(ctx, it))
            dao.insertOne(newBook)
            viewModel.shelfList.add(newBook)
            log("new add book into db: $originalFileName")
        }else{
            dao.updateOne(newBook)
            log("update book into db: $originalFileName")
        }
    }
}


//主屏
@Composable
fun ScreenBookShelf(call: ScreenCall){
    val scope = rememberCoroutineScope()

    val ctx =  LocalContext.current
    val dao = LocalDao.current

    //val snackbarHostState = remember { SnackbarHostState() }
    val viewModel: MyViewModel = LocalViewModel.current

    val (delRow, setDelRow) = remember { mutableStateOf<Book?>(null) }

    LaunchedEffect(Unit){
        if(!viewModel.shelfListLoaded){
            viewModel.shelfListLoaded = true
            val list = dao.findAll()
            viewModel.shelfList.addAll(list)
            log("${list.size} books from db added")
//          viewModel.shelfList.forEach{    it.exist = DocumentFile.fromSingleUri(ctx, it.uri)?.exists() == true   }
        }
    }

    Box(Modifier.fillMaxSize().padding(call.scaffoldPadding).background(MaterialTheme.colorScheme.primaryContainer)){
        if(viewModel.shelfList.isNullOrEmpty()){
            val filePickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenMultipleDocuments(), // OpenDocument()
                onResult = { uriList ->
                    scope.launch {
                        uriList.forEach { handleSelectedPdfUri(ctx, dao,viewModel, it) }
                    }
                } // 处理选中的 Uri
            )
            Column(Modifier.fillMaxSize(),verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
                FilledTonalButton({
                    filePickerLauncher.launch(arrayOf("application/pdf") )
                }){
                    Text(stringResource(id = R.string.add_book), Modifier.fillMaxWidth(0.5f), textAlign = TextAlign.Center)
                }
            }
        }else{
            BooksGrid(viewModel.shelfList){
                setDelRow(it)
            }

            if(delRow != null){
                AlertDialog(
                    onDismissRequest = {  },
                    dismissButton = {  TextButton(onClick = { setDelRow(null) })  {  Text(stringResource(id = R.string.cancel)) }},
                    confirmButton = {
                        TextButton(onClick = {
                            scope.launch {
                                dao.deleteOne(delRow)
                                viewModel.shelfList.remove(delRow)
                                CacheManager.delBook(ctx, delRow.id)
                            }
                            setDelRow(null)
                        }) {   Text(stringResource(id = R.string.ok))   }
                    },
                    title = { Text(stringResource(id = R.string.del_title)) },
                    text = {
                        Text(stringResource(id = R.string.del_desc))
                    }
                )
            }
        }
    }
}

//书籍竖直列表格
@Composable
fun BooksGrid(list: List<Book>, onDelOne: (b: Book)->Unit){
    LazyVerticalGrid(
        GridCells.Adaptive(minSize = 96.dp),
        Modifier.padding(15.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(list.size, {list[it].id} ) {
            BookGridItem(list[it], onDelOne)
        }
    }
}


//书籍方块项
@Composable
fun BookGridItem(b: Book, onDel: (b: Book)->Unit){
    val viewModel: MyViewModel = LocalViewModel.current
    val router = useRouter()
    val ctx =  LocalContext.current
    val (cover, setCover) = remember { mutableStateOf(if(b.hasCover == 1) b.cover(ctx) else null) }
    Column(Modifier.height(200.dp).wrapContentWidth(Alignment.CenterHorizontally)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { router.navByName(AppConstants.AppRoutes.PDFViewer, b) },
                )
            },
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        val h = 160
        val name = b.name
        Box(Modifier.fillMaxWidth().height(h.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.secondaryContainer), Alignment.Center)//elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        {
            var coverIndex = 1f
            if(viewModel.isEditingShelf.value){
                coverIndex = 0f
                BookOperations(b,  h, onDel, setCover)
            }

            //书籍封面，若没有则使用位居中间的文本
            cover?.let{
                BitmapFactory.decodeFile(cover.absolutePath)?.let{
                    //ContentScale.Crop //在较小的手机屏幕上。因pdf页面较大，导致外围不显示，只是显示bitmap的中间部分
                    //ContentScale.FillBounds // 对bitmap进行拉伸填充屏幕，会变形
                    Image(it.asImageBitmap(), name,
                        Modifier.fillMaxSize().zIndex(coverIndex), Alignment.Center, ContentScale.FillHeight)//保持横宽比
                }
            }?:Column( Modifier.fillMaxSize().zIndex(coverIndex).padding(horizontal = 3.dp),verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
                Text(name, style = MaterialTheme.typography.labelSmall ,textAlign = TextAlign.Center, overflow = TextOverflow.Ellipsis, maxLines = 3)
            }
        }

        //书的名称
        Text(name,  Modifier.fillMaxWidth().height(40.dp),
            //style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center, lineHeight = 16.sp, overflow = TextOverflow.Ellipsis, maxLines = 2)
    }
}

@Composable
fun BookOperations(b: Book, h: Int, onDel: (b: Book)->Unit, setCover: (cover: File?)->Unit){
    val ctx =  LocalContext.current
    val dao = LocalDao.current
    val scope = rememberCoroutineScope()
    //val router = useRouter()
    val (disableDarkMode, setDisableDarkMode) = remember { mutableStateOf(b.disableDarkMode == 1) }
    val quality = remember { mutableStateOf(PdfQuality.valueOf(b.quality)) }
    val updatedQuality = rememberUpdatedState(quality.value)//建立对quality的引用
    Column(
        Modifier.fillMaxWidth(0.8f).fillMaxHeight(0.8f).clip(RoundedCornerShape(4.dp)).zIndex(2f).background(MaterialTheme.colorScheme.surfaceVariant).alpha(0.5f), Arrangement.SpaceEvenly, Alignment.Start){
        //封面操作
        BookOperation(if(b.hasCover == 1) Icons.Rounded.PhotoSizeSelectLarge else Icons.Rounded.AddPhotoAlternate, stringResource(if(b.hasCover == 1) R.string.cancel_cover else R.string.first_page_cover) ){
            scope.launch {
                if(b.hasCover == 0){
                    val f = PdfPageLoader.loadFirstPageAsCover(b.id, ctx, h)//create cover file
                    if(f != null){
                        b.hasCover = 1
                        setCover(f)
                    }
                }else{
                    b.hasCover = 0
                    setCover(null)
                    CacheManager.delCover(ctx, b.id)//delete cover file
                }
                log("update into db cover: $b")
                dao.updateOne(b)
            }
        }

        //暗黑模式切换
        BookOperation(if(disableDarkMode) Icons.Rounded.Brightness4  else Icons.Rounded.Brightness1, stringResource(if(disableDarkMode) R.string.enable_dark_mode else R.string.disable_dark_mode) ){
            scope.launch {
                if(b.disableDarkMode == 1){
                    b.disableDarkMode = 0
                    setDisableDarkMode(false)
                }else{
                    b.disableDarkMode = 1
                    setDisableDarkMode(true)
                }
                dao.updateOne(b)
            }
        }

        //切换quality BookOperation后面跟lambda，每次运行时里面捕捉到的quality变量值是运行时上次更新的quality生效之前的值
        BookOperation(Icons.Rounded.LensBlur, stringResource(quality2ResId(quality.value)) ){
            scope.launch {
                //这里必须使用updatedQuality，不能使用quality，因为lambda中捕捉到的变量值都是上一次更新生效之前的值，而不是更新后的值，
                // 通过使用引用，可以使用最新值。updatedQuality相当于引用，通过它访问，总是得到最新的
                //如果使用quality，捕捉到的值总是Middle从而newOne总是High
                val newOne = when(updatedQuality.value){
                    PdfQuality.Low -> PdfQuality.Middle
                    PdfQuality.Middle -> PdfQuality.High
                    PdfQuality.High -> PdfQuality.Low
                }
                //
                log("click: quality=${quality.value}, new quality=$newOne")
                quality.value = newOne
                CacheManager.delCachedPages(ctx, b.id)
                b.quality = newOne.name
                dao.updateOne(b)
            }
        }

        //删除操作
        BookOperation(Icons.Rounded.Delete, stringResource(R.string.del) ){
            onDel(b)
        }
    }
}

fun quality2ResId(quality: PdfQuality) = when(quality)
{
    PdfQuality.Low -> R.string.quality_l
    PdfQuality.Middle -> R.string.quality_m
    PdfQuality.High -> R.string.quality_h
}

@Composable
fun BookOperation(icon: ImageVector, label: String, onClick: ()->Unit){
    Row(Modifier.fillMaxWidth().wrapContentHeight().padding(horizontal = 3.dp).pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                )
            }, Arrangement.spacedBy(6.dp), Alignment.CenterVertically){
        Icon(icon, label, Modifier.size(16.dp))
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 12.sp, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
    }
}