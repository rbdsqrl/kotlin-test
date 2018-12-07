package com.zopsmart.platformapplication.repository

import android.content.Context
import com.zopsmart.platformapplication.exception.SessionExpiredException
import com.zopsmart.platformapplication.exception.ZSException
import com.zopsmart.platformapplication.extension.isNetworkAvailable
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.TimeUnit

open class Repository(var context: Context) {
    companion object {
        const val DATA = "data"
        const val STORE_ID = "storeId"
        const val ID = "id"
        const val PAGE = "page"
        const val LAYOUTS = "layouts"
        const val NAME = "name"
        const val TITLE = "title"
        const val VALUE = "value"
        const val METADATA = "metadata"
        const val COUNT = "count"
        const val OFFSET = "offset"
        const val LIMIT = "limit"
        const val COLLECTION = "collection"
        const val POSITION = "position"
        const val PRODUCT = "product"
    }
    var appDatabase = AppDatabase.getAppDatabase(context)

    enum class Method {
        GET, POST, PUT, DELETE
    }

    inner class ZSRequest(
        var method: Method,
        var url: String,
        var headers: HashMap<String, String>?,
        var params: JSONObject?
    ) {

        private var httpUrl: HttpUrl
        private var httpClient: OkHttpClient
        private var headerBuild: Headers
        private var requestBody: RequestBody?

        init {
            val builder: OkHttpClient.Builder = OkHttpClient.Builder()
            builder.connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
            httpClient = builder.build()
            httpUrl = HttpUrl.parse(url)!!
            headerBuild = getRequestHeaders()
            requestBody = getRequestBody()
        }

        private fun getRequestBody(): RequestBody? {
            if (method != Method.GET || params != null) {
                //For GO API, request body needs to be sent as JSON in PUT/POST requests.
                if (params == null) params = JSONObject()
                if (LocalStorage.init(context).get(LocalStorage.PREFERRED_STORE, 0) != 0)
                    params!!.put(STORE_ID, LocalStorage.init(context).get(LocalStorage.PREFERRED_STORE, 0))
                val JSON = MediaType.parse("application/json")
                return RequestBody.create(JSON, params.toString())
            }
            return null
        }

        private fun getRequestHeaders(): Headers {
            if (headers == null)
                headers = java.util.HashMap()

            val accessToken = LocalStorage.init(context).get(LocalStorage.ACCESS_TOKEN, null as String?)

            if (accessToken != null) {
                headers!!["Authorization"] = "Bearer $accessToken"
            }

            headers!!["User-Agent"] = getUserAgent()

            if (this.method != Method.GET) headers!!["content-type"] = "application/json"

            return Headers.of(headers)
        }

        fun addQueryParameter(key: String, value: String) {
            httpUrl = httpUrl.newBuilder().addQueryParameter(key, value).build()
        }

        @Throws(Exception::class)
        fun fetch(): JSONObject {
            if (!context.isNetworkAvailable()) {
                throw ZSException("NO INTERNET CONNECTION")
                //throw exception
            }
            //Add store id if it is not equal to the default value
            val storeId = LocalStorage.init(context).get(LocalStorage.PREFERRED_STORE, 0L)
            if (storeId != 0L)
                httpUrl = httpUrl.newBuilder().addQueryParameter(STORE_ID, "$storeId").build()
            val requestBuilder = Request.Builder().headers(headerBuild).url(httpUrl)
            when (method) {
                Method.POST -> requestBuilder.post(requestBody!!)
                Method.GET -> requestBuilder.get()
                Method.PUT -> requestBuilder.put(requestBody!!)
                Method.DELETE -> requestBuilder.delete(requestBody!!)
            }

            val request = requestBuilder.build()
            val response: Response
            try {
                response = httpClient.newCall(request).execute()
            } catch (e: IOException) {
                throw ZSException("TIMEOUT ERROR MESSAGE")
            }
            val responseBody = response.body()
            if (!response.isSuccessful) {
                //TODO USE A UTIL TO PARSE THE ERROR MESSAGE FROM THE RESPONSE
                if (response.code() == 401)
                    throw SessionExpiredException("error message after parsing")
                throw ZSException("error message after parsing")
            }
            val result = responseBody?.string()
            return JSONObject(result)
        }
    }

    //TODO
    private fun getUserAgent(): String {
        return "User agent"
    }
}