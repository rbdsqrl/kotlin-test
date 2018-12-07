package com.zopsmart.platformapplication.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.zopsmart.platformapplication.repository.TypeConverter

@Entity
@TypeConverters(TypeConverter::class)
data class CartItem(
    @PrimaryKey var id: Long,
    val itemId: Long,
    var quantity: Int,
    val stock: Long,
    var name: String,
    var fullName: String,
    var brand: String,
    var image: String,
    @field:TypeConverters(TypeConverter::class) var images: ArrayList<String>,
    var url: String,
    var mrp: Double,
    var discount: Double,
    var sellingPrice: Double
)