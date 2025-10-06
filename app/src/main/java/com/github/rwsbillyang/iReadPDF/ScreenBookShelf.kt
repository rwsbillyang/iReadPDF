package com.github.rwsbillyang.iReadPDF

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
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
import com.github.rwsbillyang.iReadPDF.AppConstants.TAG
import com.github.rwsbillyang.iReadPDF.db.Book
import com.github.rwsbillyang.iReadPDF.db.MyDao
import com.github.rwsbillyang.iReadPDF.db.db
import com.github.rwsbillyang.iReadPDF.pdfview.CacheManager
import com.github.rwsbillyang.iReadPDF.pdfview.FileUtil
import com.github.rwsbillyang.iReadPDF.pdfview.PdfPageLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun BookShelfToolIcons(){
    val ctx =  LocalContext.current
    val viewModel: MyViewModel = LocalViewModel.current
    val scope = rememberCoroutineScope()
    val dao = db(ctx).dao()

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
    TextButton(onClick = {
        filePickerLauncher.launch(arrayOf("application/pdf") )
    }) {
        Icon(Icons.Rounded.Add, contentDescription = "add books")
        //Text("Add Books")
    }

    TextButton(onClick = {
        viewModel.isEditingShelf.value = !viewModel.isEditingShelf.value
    }) {
        Icon(Icons.Rounded.Edit, contentDescription = "Manage")
        //Text("Manage")
    }
}

// 处理选中的 PDF Uri（包含 MD5 计算和缓存）
suspend fun handleSelectedPdfUri(ctx: Context, dao: MyDao, viewModel: MyViewModel, uri: Uri) {
    FileUtil.calculateMd5(ctx, uri)?.let {
        val b = dao.findOne(it)
        val originalFileName = FileUtil.getFileNameFromUri(ctx, uri) ?: b?.name?: "unknown.pdf"
        val newBook = Book(it, originalFileName)
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


@Composable
fun ScreenBookShelf(call: ScreenCall){
    //Log.d(AppConstants.TAG, "enter ScreenBookShelf")

    val scope = rememberCoroutineScope()

    val ctx =  LocalContext.current
    val dao = db(ctx).dao()

    val snackbarHostState = remember { SnackbarHostState() }
    val viewModel: MyViewModel = LocalViewModel.current

    val (delRow, setDelRow) = remember { mutableStateOf<Book?>(null) }

    LaunchedEffect(Unit){
        if(!viewModel.shelfListLoaded){
            viewModel.shelfListLoaded = true
            val list = dao.findAll()
            viewModel.shelfList.addAll(list)
            Log.d(TAG, "${list.size} books from db added")
//            viewModel.shelfList.forEach{
//                it.exist = DocumentFile.fromSingleUri(ctx, it.uri)?.exists() == true
//            }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .padding(call.scaffoldPadding)){
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
                TextButton({
                    filePickerLauncher.launch(arrayOf("application/pdf") )
                }){
                    Text("Add Books",color = MaterialTheme.colorScheme.primary)
                }
            }
        }else{
            BooksGrid(viewModel.shelfList){
                setDelRow(it)
            }

            if(delRow != null){
                AlertDialog(
                    onDismissRequest = {  },
                    dismissButton = {  TextButton(onClick = { setDelRow(null) })  {  Text("Cancel") }},
                    confirmButton = {
                        TextButton(onClick = {
                            scope.launch {
                                dao.deleteOne(delRow)
                                viewModel.shelfList.remove(delRow)
                                CacheManager.delBook(ctx, delRow.id)
                            }
                            setDelRow(null)
                        }) {   Text("OK")   }
                    },
                    title = { Text(text = "are your sure to delete from book shelf？") },
                    text = {
                        Text("can re-add it after delete")
                    }
                )
            }
        }
    }
}

@Composable
fun BooksGrid(list: List<Book>, onDelOne: (b: Book)->Unit){
    LazyVerticalGrid(
        GridCells.Adaptive(minSize = 96.dp),
        Modifier.padding(15.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(list.size, {list[it].id} ) {
            GridItem(list[it], onDelOne)
        }
    }
}



@Composable
fun GridItem(b: Book, onDel: (b: Book)->Unit){
    val viewModel: MyViewModel = LocalViewModel.current
    val router = useRouter()
    val ctx =  LocalContext.current
    val dao = db(ctx).dao()
    val scope = rememberCoroutineScope()
    val (cover, setCover) = remember { mutableStateOf(if(b.hasCover == 1) b.cover(ctx) else null) }
    Column(
        Modifier
            .height(200.dp)
            .wrapContentWidth(Alignment.CenterHorizontally)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { router.navByName(AppRoutes.PDFViewer, b) },
                )
            },
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally)
    {
         val h = 160
        //去掉文件名称后面的.pdf扩展名（不分大小写），同时文件名称中保留原始大小写
        val name = if(b.name.substringAfterLast('.').lowercase() == "pdf")b.name.substringBeforeLast('.') else b.name
        Box(Modifier.fillMaxWidth().height(h.dp))//elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        {

            var coverIndex = 1f
            if(viewModel.isEditingShelf.value){
                coverIndex = 0f

                Row(Modifier.fillMaxWidth().zIndex(1f), Arrangement.SpaceBetween){
                    IconButton(onClick = {
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
                    }) {
                        if(b.hasCover == 1){
                            Icon(Icons.Rounded.Cancel, stringResource(R.string.cancel_cover), Modifier.size(24.dp))
                        }else{
                            Icon(Icons.Rounded.PhotoCamera, stringResource(R.string.first_page_cover), Modifier.size(24.dp))
                        }
                    }

                    IconButton(onClick = { onDel(b) }) {
                        Icon(Icons.Rounded.Delete, "Delete book", Modifier.size(24.dp))
                    }
                }
            }


            cover?.let{
                BitmapFactory.decodeFile(cover.absolutePath)?.let{
                    Image(
                        it.asImageBitmap(), name,
                        Modifier.fillMaxSize().zIndex(coverIndex),
                        Alignment.Center,
                        ContentScale.FillHeight, //保持横宽比
                        //ContentScale.Crop //在较小的手机屏幕上。因pdf页面较大，导致外围不显示，只是显示bitmap的中间部分
                        //ContentScale.FillBounds // 对bitmap进行拉伸填充屏幕，会变形
                    )
                }
            }?:Box(Modifier.fillMaxSize().padding(10.dp).zIndex(coverIndex), Alignment.Center){
                Text(name, style = MaterialTheme.typography.labelSmall ,textAlign = TextAlign.Center, overflow = TextOverflow.Ellipsis, maxLines = 3)
            }
        }
        Text(name,
            Modifier.fillMaxWidth().height(40.dp),
            //style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2)
    }
}