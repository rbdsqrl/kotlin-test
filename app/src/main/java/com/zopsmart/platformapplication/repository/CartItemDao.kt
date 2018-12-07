package com.zopsmart.platformapplication.repository

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.zopsmart.platformapplication.pojo.CartItem
import java.util.*

@Dao
interface CartItemDao {
    @Insert(onConflict = REPLACE)
    fun insert(cartItem: CartItem): Long

    @Delete
    fun delete(cartItem: CartItem)

    @Query("DELETE FROM CartItem")
    fun clearCart()

    @Insert(onConflict = REPLACE)
    fun insertAll(cartItems: ArrayList<CartItem>)

    @Query("SELECT * FROM cartitem")
    fun getAllCartItemsLiveData(): LiveData<List<CartItem>>

    @Query("SELECT * FROM cartitem")
    fun getAllCartItems(): List<CartItem>

    @Query("select * from cartitem where id = :id")
    fun getCartItemWithId(id: Long): CartItem

    @Query("select SUM(mrp *  quantity) from cartitem")
    fun getCartPrice(): LiveData<Double>

    @Query("select SUM(discount * quantity) from cartitem")
    fun getCartDiscount(): LiveData<Double>

    @Query("select SUM((mrp - discount) * quantity) from cartitem")
    fun getFinalPrice(): LiveData<Double>

    @Query("select count(*) from cartitem")
    fun getNoOfCartItems(): LiveData<Int>
}