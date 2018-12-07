package com.zopsmart.platformapplication.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Cache(
    @PrimaryKey var url: String,
    var response: String,
    var buildVersionCode: Int,
    var timestamp: Long
)