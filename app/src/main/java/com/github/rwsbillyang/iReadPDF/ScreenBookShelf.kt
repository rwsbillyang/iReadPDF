package com.github.rwsbillyang.iReadPDF

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import com.github.rwsbillyang.composerouter.ScreenCall
import com.github.rwsbillyang.composerouter.useRouter
import com.github.rwsbillyang.composeui.ProColumn
import com.github.rwsbillyang.composeui.SimpleDataTable
import com.github.rwsbillyang.iReadPDF.db.Book
import com.github.rwsbillyang.iReadPDF.db.MyDao
import com.github.rwsbillyang.iReadPDF.db.db
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
                uriList.forEach { handleSelectedPdfUri(ctx, dao,viewModel, it) }
            }
        } // 处理选中的 Uri
    )
    Spacer(Modifier.width(8.dp))
    IconButton(onClick = {
        filePickerLauncher.launch(arrayOf("application/pdf") )
    }) {
        Icon(Icons.Rounded.Add, contentDescription = "add books")
    }
}

// 处理选中的 PDF Uri（包含 MD5 计算和缓存）
suspend fun handleSelectedPdfUri(ctx: Context, dao: MyDao, viewModel: MyViewModel, uri: Uri) {
    UriFileUtil.calculateMd5(ctx, uri)?.let {
        val b = dao.findOne(it)
        val originalFileName = UriFileUtil.getFileNameFromUri(ctx, uri) ?: b?.name?: "unknown.pdf"
        val newBook = Book(it, originalFileName, uri.toString(), 1)
        if(b == null){
            dao.insertOne(newBook)
            viewModel.shelfList.add(newBook)
        }else{
            dao.updateOne(newBook)
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
    var selectedRowIndex by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit){
        viewModel.shelfList.addAll(dao.findAll())
        viewModel.shelfList.forEach{
            it.exist = DocumentFile.fromSingleUri(ctx, it.uri)?.exists() == true
        }
    }

    val router = useRouter()

    Box(Modifier.fillMaxSize().padding(call.scaffoldPadding)){
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
                    Text("Add Books")
                }
            }
        }else{
            SimpleDataTable(
                listOf(
                    ProColumn("PDF", {it.name}, 50),
                    ProColumn("进度", {if(it.total == null) "${it.page}" else "${it.page}/${it.total}" },15),
                    ProColumn("操作", weight = 35){ row, rowIndex, _ ->
                        Row(modifier = Modifier.fillMaxWidth(), Arrangement.Center, Alignment.CenterVertically){
                            IconButton(onClick = {
                                selectedRowIndex = rowIndex
                                scope.launch {
                                    withContext(Dispatchers.Main) {
                                        try {
                                            //在IO线程里，不能在UI主线程里，否则阻塞了主线程的Compose，不能重新绘制，即loading不能显示
                                            router.navByName(AppRoutes.PDFViewer, row)
                                        }catch (e: Exception){
                                            selectedRowIndex = null
                                            Log.d(AppConstants.TAG, row.toString())
                                            //Toast.makeText(ctx, "日期时间格式错误: "+ e.message, Toast.LENGTH_LONG).show()
                                            snackbarHostState.showSnackbar("日期时间格式错误: "+ e.message)
                                        }
                                    }
                                }
                            }, enabled = selectedRowIndex != rowIndex) {
                                if(selectedRowIndex == rowIndex)
                                    Icon(Icons.Outlined.HourglassEmpty, contentDescription = "wait")
                                else
                                    Icon(Icons.Outlined.Visibility, contentDescription = "Read")
                            }

                            IconButton(onClick = { setDelRow(row) }) {
                                Icon(Icons.Outlined.Delete, contentDescription = "Delete")
                            }
                        }
                    }
                ), viewModel.shelfList
            )

            if(delRow != null){
                AlertDialog(
                    onDismissRequest = {  },
                    dismissButton = {  TextButton(onClick = { setDelRow(null) })  {  Text("Cancel") }},
                    confirmButton = {
                        TextButton(onClick = {
                            scope.launch {
                                dao.deleteOne(delRow)
                                viewModel.shelfList.remove(delRow)
                            }
                            setDelRow(null)
                        }) {   Text("OK")   }
                    },
                    title = { Text(text = "are your sure to delte？") },
                    text = {
                        Text("cannot restore after delete！")
                    }
                )
            }
        }
    }
}

