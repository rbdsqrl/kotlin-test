package com.zopsmart.platformapplication.repository

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.zopsmart.platformapplication.pojo.Cache
import com.zopsmart.platformapplication.pojo.CartItem
import com.zopsmart.platformapplication.pojo.Customer

@Database(entities = [CartItem::class, Customer::class, Cache::class], version = 1 )
@TypeConverters(TypeConverter::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getCartItemDao(): CartItemDao

    abstract fun getCustomerDao(): CustomerDao

    abstract fun getCacheDao(): CacheDao

    companion object {
        private var appDatabase: AppDatabase? = null

        fun getAppDatabase(context: Context): AppDatabase? {
            if (appDatabase == null) {
                appDatabase = Room.databaseBuilder<AppDatabase>(context.applicationContext, AppDatabase::class.java, "platform")
                    .allowMainThreadQueries()
                    .build()
            }
            return appDatabase
        }

        fun destroyAppDatabase() {
            appDatabase = null
        }
    }
}