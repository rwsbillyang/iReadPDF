package com.github.rwsbillyang.iReadPDF.db

import androidx.room.*



@Dao
interface MyDao {

    // db.execSQL("PRAGMA wal_checkpoint;");
    //@RawQuery
    //suspend fun execSQL(sql: SupportSQLiteQuery) //must return a non-void type.

    @Query("SELECT COUNT(*) FROM Book")
    suspend fun count(): Long

    @Query("SELECT * FROM Book where id=:id")
    suspend fun findOne(id: String): Book?


    @Query("SELECT * FROM Book ORDER BY lastOpen DESC")
    suspend fun findAll(): List<Book>

    @Insert
    suspend fun insertAll(vararg entities: Book)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOne(e: Book)

    @Update
    suspend fun updateOne(e: Book)

    @Delete
    suspend fun deleteOne(e: Book)
}
