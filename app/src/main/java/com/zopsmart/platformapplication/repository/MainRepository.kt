package com.zopsmart.platformapplication.repository

import android.content.Context
import com.zopsmart.platformapplication.ZSParser
import org.json.JSONObject

class MainRepository(context: Context): Repository(context) {

    //TODO (link: String, page: Int) is this needed
    fun load(link: String): JSONObject {
        val response = loadData(link)
        return ZSParser.getObjectifiedResponse(response)
    }

    private fun loadData(link: String): JSONObject {
        val request = ZSRequest(Method.GET, link, null, null)
        return request.fetch()
    }


}