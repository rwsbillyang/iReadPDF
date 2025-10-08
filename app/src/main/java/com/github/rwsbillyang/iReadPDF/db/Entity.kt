package com.github.rwsbillyang.iReadPDF.db


import android.content.Context
import android.net.Uri
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.github.rwsbillyang.iReadPDF.R
import com.github.rwsbillyang.iReadPDF.pdfview.CacheManager
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


/**
 * ！！！注意
 * 1. 修改数据库schema时，要么需要升级版本号，要么卸载APP重新安装！！！assets中的db若有（如schema或数据）修改更新，需卸载app或清除其缓存，才会生效
 * 因为只是第一次才会从assets下进行copy数据库文件，以后不再copy，故修改更新后需重置
 * 2. 增加表需要在AppDatabase注解中增加其class
 * 3. 表中的字段，尽量不要用数据库的保留关键字，如：key， from等
 * 4. Entity定义为非空字段，数据库schema也要非空，需要完全对应上，注意：Entity定义中非空字段，不管有没有缺省值，数据库中都需要NOT NULL
 *
 *  在修改了数据库内容后，执行进行同步到主db上，避免再wal中：pragma wal_checkpoint(full)
 * **/

enum class PdfQuality(val id: Int){Low(R.string.quality_l), Middle(R.string.quality_m), High(R.string.quality_h),}

/**
 * pdf file要么通过uri指定，通常再次启动app后有打开权限问题。
 * 要么复制一份后，通过file进行打开，此时为为空，本地LocalFile为默认的文件名，默认路径，通过pdfFile函数获取
 * 实际情况为多用后一种方案，viewModel中创建pdfPageLoader时自动判断，使用后一种方式创建
 * 若是第三方发来intent调用，则是用第一种方式即通过uri打开
 * */
@Serializable
@Entity
class Book(
    @PrimaryKey//(autoGenerate = true)
    val id: String,//  md5(file content)
    var name: String, // file name
    val uriStr: String? = null, // should be null if it is a file, otherwise uri
    val format: String = "pdf",
    var total: Int? = null, // total pages
    var author: String? = null,
    var publisher: String? = null,


    //对于文字版pdf，黑色模式下其背景色为黑，文字也为黑，故对其bitmap像素进行取反操作，从而文字颜色变白，可以正常阅读
    // 而图片扫描格式的PDF，可正常显示，无需进行位取反，禁用黑色模式，避免位像素进行取反运算
    //对于纸质扫描图片格式的pdf，禁用黑色模式 在书架中进行对book进行设置
    var disableDarkMode: Int = 0, //if 1, disable dark mode

    var quality: String = PdfQuality.Middle.name, //0: Low, 1: Middler, 2: high

    var hasCover: Int = 0, //first page could be cover
    var fullScreen: Int = 0,
    var page: Int = 0,// current reading page number
    var pageOffset: Int = 0,// listState.firstVisibleItemScrollOffset

    var zoom: Float = 1.0f, //TransformState current reading zoom level
    var offsetX: Float = 0.0F, //TransformState offsetChange
    var offsetY: Float = 0.0F, //TransformState offsetChange
    var rotation: Int = 0, //TransformState 但由工具栏命令控制

    //设置orientation时引起configuration变化，横屏时将导致book shelf, settings等的变化，以及路由的恢复，
    //故放弃此种，采用rotation旋转90度
    @Deprecated("use rotation instead") var landscape: Int = 0, //current reading 0: portrait, 1: landscape

    var lastOpen: Long = 0, // last open time, utc
) {
    @Ignore
    var cachePages: Boolean = true

    //@get:Ignore
    val uri: Uri?
        get() = uriStr?.let{Uri.parse(it)}

    //PdfPageLoader中的loadFirstPageAsCover创建cover时将其创建到特定位置
    fun cover(ctx: Context) = CacheManager.defaultCover(ctx, id)

    //BookShelf 添加书籍的处理，handleSelectedPdfUri中FileUtil的copy函数，copy到指定位置
    fun pdfFile(ctx: Context) = CacheManager.defaultPdfFile(ctx, id)

    override fun toString() = Json.encodeToString(serializer(), this)

}

