package com.zopsmart.platformapplication.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.zopsmart.platformapplication.repository.TypeConverter

@Entity
data class Customer(
    @PrimaryKey var id: Long,
    var name: String,
    var imageUrl: String,
    var phone: String,
    var email: String,
    @field:TypeConverters(TypeConverter::class) var defaultAddress: Address,
    @field:TypeConverters(TypeConverter::class) var addresses: ArrayList<Address>,
    var customerString: String
)