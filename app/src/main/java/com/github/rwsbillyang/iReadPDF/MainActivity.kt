package com.github.rwsbillyang.iReadPDF

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.sqlite.db.SimpleSQLiteQuery
import com.github.rwsbillyang.composerouter.nav.LocalRoutableActivity
import com.github.rwsbillyang.composerouter.nav.NavScaffold3
import com.github.rwsbillyang.iReadPDF.db.Book
import com.github.rwsbillyang.iReadPDF.db.db
import com.github.rwsbillyang.iReadPDF.db.syncDb
import com.github.rwsbillyang.iReadPDF.pdfview.FileUtil
import com.github.rwsbillyang.iReadPDF.ui.theme.MyAppTheme
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.FileOutputStream


//https://developer.android.google.cn/jetpack/compose/compositionlocal?hl=zh-cn
//一种错误做法的示例是创建包含特定屏幕的 ViewModel 的 CompositionLocal，以便该屏幕中的所有可组合项都可以获取对 ViewModel 的引用来执行某些逻辑。
// 这是一种错误做法，因为并非特定界面树下的所有可组合项都需要知道 ViewModel。
// 最佳做法是遵循状态向下传递而事件向上传递的模式，只向可组合项传递所需信息。这样做会使可组合项的可重用性更高，并且更易于测试。
//注: 在编写通用Composable库时，不应该使用LocalViewModal。但本例中，各Composable都需要ViewModel，恰恰与上面说法相反，故使用这种方式
val LocalViewModel = staticCompositionLocalOf{ MyViewModel() }


class MainActivity : LocalRoutableActivity() { //use local router if use LocalRoutableActivity

    private val viewModel : MyViewModel by viewModels()


    override fun getRoutes() = getAppRoutes()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CompositionLocalProvider(LocalViewModel provides viewModel) {
                        NavScaffold3(R.string.app_name)
                    }
                }
            }
        }
        handleIntent(intent)
    }
    override fun onPause() {
        super.onPause()
        log("onPause,saveCurrentBook")
        saveCurrentBook()
    }

    override fun onDestroy() {
        super.onDestroy()
        //saveCurrentBook()
        log("onDestroy, releasePdfLoader and shelf list")
        viewModel.releasePdfLoader()
        viewModel.releaseShelfList()
        syncDb(this)
    }

    private fun saveCurrentBook(){

        val dao = db(this).dao()
        // 保存当前阅读进度
        lifecycleScope.launch {
            viewModel.currentBook?.let {
                log("save currentBook: $it")
                val sharedPreferences = getSharedPreferences("iRead", Context.MODE_PRIVATE)
                // sharedPreferences.edit().remove(AppConstants.KEY_CURRENT).apply()
                sharedPreferences.edit().putString(AppConstants.KEY_CURRENT, it.id).apply()

                dao.updateOne(it)
            }

//            val count = dao.count()
//            log("db books count: $count")
        }
    }
    private fun handleIntent(intent: Intent) {
        when {
            intent.action == Intent.ACTION_VIEW -> {
                // 从文件管理器或其他应用打开PDF
                intent.data?.let { uri ->
                    jumpToTmpBook(uri)
                }
            }
            intent.type == "application/pdf" -> {
                // 处理其他PDF相关的Intent
                intent.data?.let { uri ->
                    jumpToTmpBook(uri)
                }
            }
            else -> {
                val dao = db(this).dao()
                // 打开last reading book
                lifecycleScope.launch {
                    val sharedPreferences = getSharedPreferences("iRead", Context.MODE_PRIVATE)
                    sharedPreferences.getString(AppConstants.KEY_CURRENT, null)?.let{
                       val b = dao.findOne(it)
                       if(b == null){
                           localRouter.navByName(AppRoutes.BookShelf)
                       }else{
                           log("open last read book: $b")
                           this@MainActivity.requestedOrientation = if(b.landscape == 1) ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

                           localRouter.navByName(AppRoutes.PDFViewer, b)
                       }
                    }?: localRouter.navByName(AppRoutes.BookShelf)
                }
            }
        }
    }

    private fun jumpToTmpBook(uri: Uri){
        val ctx = this
        MainScope().launch {
            val id = FileUtil.calculateMd5(ctx, uri)//view model中创建PdfPageLoader通过uri打开，而不是file打开
            if(id != null){
                val originalFileName = FileUtil.getFileNameFromUri(ctx, uri) ?: "unknown.pdf"
                val tmpBook = Book(id, originalFileName, uri.toString()).apply {
                    cachePages = false //临时打开的pdf不缓存其页面
                }
                localRouter.navByName(AppRoutes.PDFViewer, tmpBook)
            }
        }
    }

//    private fun initSettingsValue(ctx: Context, viewModel: MyViewModel){
//        this.lifecycleScope.launch {
//            val p = dataStore.data.first()
////            viewModel.enableZheng14.value = p[booleanPreferencesKey(SettingsKey.Zheng14)]?:true
////            viewModel.enableFuZuo.value = p[booleanPreferencesKey(SettingsKey.FuZuo)]?:true
//        }
//    }


}

