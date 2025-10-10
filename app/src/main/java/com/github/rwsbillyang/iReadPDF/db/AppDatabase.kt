package com.github.rwsbillyang.iReadPDF.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.FileOutputStream

@Database(entities = [
    Book::class,
], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): MyDao

    companion object {
        private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app.db"
                )//.setJournalMode(RoomDatabase.JournalMode.AUTOMATIC)
                    .build()//.createFromAsset("app.db")//有预置数据时需要
                INSTANCE = instance
                instance
            }
        }

        /**
         * 添加完books到数据库，再退出app后，再clear app历史记录，导致添加到db中的books丢失
         * 若不清除系统刚使用过的app，则不会丢失
         * 问题依然如此，在huawei pad也是同样的问题
         * TODO：诡异的是，在MainActivity.onDestroy中saveCurrentBook并调用此close后恢复正常，不丢失数据
         * 然后再修改回原代码，不调用此函数，saveCurrentBook放回到onPause中，不再close，注释掉此函数，依旧正常
         * 编译环境，清除缓存的问题？但也没有明显的清除编译环境或更换环境呀
         * 就像使用ComposeRouter时总提示 Navbar 这个composable函数转换成普通的Function异常，经过 gradle clean ComposeRouter后恢复正常一样？
         *
         * 还有Eyesight这个app 最先只能targetSDK 13，否则scale时锯齿，后来又突然好了，任何targetSDK都正常？
         * */
        fun close(){
            synchronized(this){
                INSTANCE?.apply {
                    FileOutputStream(openHelper.writableDatabase.path).apply {
                        flush()
                        fd.sync()
                    }
                    if(isOpen)close()
                }
                INSTANCE = null
            }
        }
    }
}

//fun db(ctx: Context) = AppDatabase.getInstance(ctx)



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
