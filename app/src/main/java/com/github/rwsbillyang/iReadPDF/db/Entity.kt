package com.github.rwsbillyang.iReadPDF.db


import android.net.Uri
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable


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


/**
 *
 * */
@Serializable
@Entity
class Book(
    @PrimaryKey//(autoGenerate = true)
    val id: String,//  md5(file content)
    val name: String, // file name
    val uriStr: String, // absolute path + file
    val inShelf: Int = 0, //in book shelf: 1, or tmp : 0
    val format: String = "pdf",

    var page: Int = 0,// current reading page number
    var total: Int? = null, // total pages
    var zoom: Float = 1.0f, //current reading zoom level
    var offsetX: Float = 0.0F, //current reading scroll
    var offsetY: Float = 0.0F, //current reading scroll
    var landscape: Int = 0, //current reading 0: portait, 1: landscape
    var lastOpen: Long = 0, // last open time, utc
) {
    @Ignore
    var exist: Boolean = true

    //@get:Ignore
    val uri
        get() = Uri.parse(uriStr)

}

