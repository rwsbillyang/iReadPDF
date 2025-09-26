package com.github.rwsbillyang.iReadPDF.db

import androidx.room.*
//import androidx.sqlite.db.SupportSQLiteQuery


@Dao
interface MyDao {

//    @RawQuery
//    suspend fun findFromYunMiscStarsToStopGong(sql: SupportSQLiteQuery): List<YunMiscStarsToStopGong>


    @Query("SELECT * FROM Book where id=:id")
    suspend fun findOne(id: String): Book?


    @Query("SELECT * FROM Book ORDER BY lastOpen DESC")
    suspend fun findAll(): List<Book>

    @Insert
    suspend fun insertAll(vararg entities: Book)
    @Insert
    suspend fun insertOne(e: Book)

    @Update
    suspend fun updateOne(e: Book)

    @Delete
    suspend fun deleteOne(e: Book)
}
