package com.zopsmart.platformapplication.repository

import android.content.Context
import com.zopsmart.platformapplication.pojo.Cache

class CacheRepository(context: Context): Repository(context) {
    val cacheDao = appDatabase?.getCacheDao()

    fun insert(cache: Cache) {
        cacheDao?.insert(cache)
    }

    fun getCache(url: String): Cache? {
        return cacheDao?.getCache(url)
    }

    fun deletePreviousVersionCache(currentVersion: Int) {
        cacheDao?.deleteFromPreviousVersion(currentVersion)
    }
}