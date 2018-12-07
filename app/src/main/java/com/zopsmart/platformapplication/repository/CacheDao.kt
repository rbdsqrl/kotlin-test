package com.zopsmart.platformapplication.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.zopsmart.platformapplication.pojo.Cache

@Dao
interface CacheDao {

    @Insert(onConflict = REPLACE)
    fun insert(cache: Cache): Long

    @Query("SELECT * FROM Cache where url = :url")
    fun getCache(url: String): Cache

    @Query("DELETE FROM Cache where buildVersionCode < :currentVersionCode")
    fun deleteFromPreviousVersion(currentVersionCode: Int)

    @Query("DELETE FROM Cache")
    fun deleteAll()

    @Delete
    fun deleteCache(cache: Cache)

    @Query("DELETE FROM Cache where url = :url ")
    fun deleteCache(url: String)
}