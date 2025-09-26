package com.github.rwsbillyang.iReadPDF.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [
    Book::class,
], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): MyDao

    companion object {
        private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "app.db"
                    ).createFromAsset("app.db")
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}

fun db(applicationContext: Context) = AppDatabase.getInstance(applicationContext)
//fun db(applicationContext: Context) = Room.databaseBuilder(
//    applicationContext,
//    AppDatabase::class.java, "app.db"
//).createFromAsset("app.db").build()
//assets中的db若有（如schema或数据）修改更新，需卸载app或清除其缓存，才会生效
//因为只是第一次才会从assets下进行copy数据库文件，以后不再copy，故修改更新后需重置

//OK：使用Android Studio的Device Manager中的Explorer找到/data/data/com.github.rwsbillyang.mingli/databases/app.db然后保存到computer中
// 但得到的db与assets中的相同

//Fail：使用App Inspector中的DataBase Inspector导出数据库或表格时，也提示Can't open offline database 应该是Android Studio的bug

//Fail：以下命令备份数据库到SD卡失败，因为没有权限
//adb -d shell 'run-as com.github.rwsbillyang.mingli cat /data/data/com.github.rwsbillyang.mingli/databases/app.db > /sdcard/mingli.sqlite'
// run-as com.github.rwsbillyang.mingli cat /data/data/com.github.rwsbillyang.mingli/databases/app.db > /sdcard/mingli.db
