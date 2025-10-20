package com.github.rwsbillyang.iReadPDF

import android.content.Context
import android.content.Intent
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
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import com.github.rwsbillyang.composerouter.nav.LocalRoutableActivity
import com.github.rwsbillyang.composerouter.nav.NavScaffold3
import com.github.rwsbillyang.iReadPDF.db.AppDatabase
import com.github.rwsbillyang.iReadPDF.db.Book
import com.github.rwsbillyang.iReadPDF.db.MyDao
import com.github.rwsbillyang.iReadPDF.pdfview.FileUtil
import com.github.rwsbillyang.iReadPDF.ui.theme.MyAppTheme
import com.github.rwsbillyang.iReadPDF.ui.theme.ThemeEnum
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch



//https://developer.android.google.cn/jetpack/compose/compositionlocal?hl=zh-cn
//一种错误做法的示例是创建包含特定屏幕的 ViewModel 的 CompositionLocal，以便该屏幕中的所有可组合项都可以获取对 ViewModel 的引用来执行某些逻辑。
// 这是一种错误做法，因为并非特定界面树下的所有可组合项都需要知道 ViewModel。
// 最佳做法是遵循状态向下传递而事件向上传递的模式，只向可组合项传递所需信息。这样做会使可组合项的可重用性更高，并且更易于测试。
//注: 在编写通用Composable库时，不应该使用LocalViewModal。但本例中，各Composable都需要ViewModel，恰恰与上面说法相反，故使用这种方式
val LocalViewModel = staticCompositionLocalOf{ MyViewModel() }
val LocalDataBase = staticCompositionLocalOf<AppDatabase>{ error("LocalDataBase not provided in composition!") }
val LocalDao = staticCompositionLocalOf<MyDao>{ error("LocalDao not provided in composition!") }


class MainActivity : LocalRoutableActivity() { //use local router if use LocalRoutableActivity

    private val viewModel : MyViewModel by viewModels()
    private lateinit var db: AppDatabase

    override fun getRoutes() = getAppRoutes()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log("onCreate, get db")
        db = AppDatabase.getInstance(this)
        setContent {
            MyAppTheme(viewModel.theme.value) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CompositionLocalProvider(
                        LocalViewModel provides viewModel,
                        LocalDataBase provides db,
                        LocalDao provides db.dao())
                    {
                        NavScaffold3(R.string.app_name)
                    }
                }
            }
        }
        //来自Confiuration自行恢复路由界面,如切换深色模式，系统语言修改等
        if(viewModel.isFromConfigurationsChanged){
            if(router.currentRoute?.name == AppConstants.AppRoutes.PDFViewer){
                viewModel.currentBook?.let {
                    router.navByName(AppConstants.AppRoutes.PDFViewer, it)
                }
            }
        }else
            handleIntent(intent)//其它情况处理Intent，打开指定pdf或上次pdf，或进入book shelf

        viewModel.isFromConfigurationsChanged = false
    }

    //Home键切换到主屏，然后再清除app任务列表，不会调用onDestroy，但会onPause，
    // 故只要离开当前界面，就保存，而不是destroy时再保存
    override fun onPause() {
        super.onPause()
        log("onPause,saveCurrentBook")
        saveCurrentBook()
    }

    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy， isChangingConfigurations=$isChangingConfigurations")
        viewModel.isFromConfigurationsChanged = isChangingConfigurations

        //若是configuration变化导致，不可release，只有back不再前台了则需release
        if(!isChangingConfigurations){
            viewModel.releasePdfLoader()
            viewModel.releaseShelfList()
        }

        //需要协程中的saveCurrentBook执行完毕才可close
        //db.close()
    }

    private fun saveCurrentBook(){
        // 保存当前阅读进度
        lifecycleScope.launch {
            viewModel.currentBook?.let {
                log("save currentBook: $it")
                val sharedPreferences = getSharedPreferences("iRead", Context.MODE_PRIVATE)
                // sharedPreferences.edit().remove(AppConstants.KEY_CURRENT).apply()
                sharedPreferences.edit().putString(AppConstants.KEY_CURRENT, it.id).apply()

                db.dao().updateOne(it)
            }

//            val count = db.dao().count()
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
                val dao = db.dao()
                val localRouter = router
                // 打开last reading book
                lifecycleScope.launch {
                    initSettingsValue(viewModel)
                    if(viewModel.enterBookDirectly){
                        val sharedPreferences = getSharedPreferences("iRead", Context.MODE_PRIVATE)
                        sharedPreferences.getString(AppConstants.KEY_CURRENT, null)?.let{
                            val b = dao.findOne(it)
                            if(b == null){
                                localRouter.navByName(AppConstants.AppRoutes.BookShelf)
                            }else{
                                log("open last read book: $b")
                                //this@MainActivity.setLandscape(b.landscape)

                                localRouter.navByName(AppConstants.AppRoutes.PDFViewer, b)
                            }
                        }?: localRouter.navByName(AppConstants.AppRoutes.BookShelf)
                    }else{
                        localRouter.navByName(AppConstants.AppRoutes.BookShelf)
                    }
                }
            }
        }
    }

    private fun jumpToTmpBook(uri: Uri){
        val ctx = this
        lifecycleScope.launch {
            val id = FileUtil.calculateMd5(ctx, uri)//view model中创建PdfPageLoader通过uri打开，而不是file打开
            if(id != null){
                val originalFileName = FileUtil.getFileNameFromUri(ctx, uri) ?: "unknown.pdf"
                val tmpBook = Book(id, originalFileName, uri.toString()).apply {
                    cachePages = false //临时打开的pdf不缓存其页面
                }
                router.navByName(AppConstants.AppRoutes.PDFViewer, tmpBook)
            }
            initSettingsValue(viewModel)
        }
    }

    private suspend fun initSettingsValue(viewModel: MyViewModel){
        val p = dataStore.data.first()
        viewModel.enterBookDirectly = p[booleanPreferencesKey(AppConstants.SettingsKey.EnterBookDirectly)]?:false
        viewModel.disableMovePdf = p[booleanPreferencesKey(AppConstants.SettingsKey.DisableMovePdf)]?:true
        viewModel.screenOn.value = p[stringPreferencesKey(AppConstants.SettingsKey.KeepScreenOn)]?.toInt()?:0
        viewModel.theme.value = p[stringPreferencesKey(AppConstants.SettingsKey.Theme)]?.let { ThemeEnum.valueOf(it) }?: ThemeEnum.Default
    }
}

