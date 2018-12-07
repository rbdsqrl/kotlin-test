package com.zopsmart.platformapplication.extension

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo


fun Context.isNetworkAvailable() : Boolean {
    val cm: ConnectivityManager = this.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val netInfo: NetworkInfo? = cm.activeNetworkInfo
    return netInfo != null && netInfo.isConnected
}
