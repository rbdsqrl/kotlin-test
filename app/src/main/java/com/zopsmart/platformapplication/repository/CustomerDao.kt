package com.zopsmart.platformapplication.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.zopsmart.platformapplication.pojo.Customer

@Dao
interface CustomerDao {

    @Insert(onConflict = REPLACE)
    fun insert(customer: Customer): Long

    @Delete
    fun delete(customer: Customer)

    @Query("DELETE FROM Customer")
    fun deleteAll()

    @Query("SELECT * FROM customer limit 1")
    fun getUser(): Customer
}