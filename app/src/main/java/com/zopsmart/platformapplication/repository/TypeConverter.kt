package com.zopsmart.platformapplication.repository

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zopsmart.platformapplication.pojo.Address
import java.util.*
import kotlin.collections.ArrayList

class TypeConverter {
    companion object {
        private const val DELIMITER = ","

        @TypeConverter
        @JvmStatic
        fun toArrayList(imagesString: String): ArrayList<String> {
            return ArrayList(Arrays.asList(*imagesString.split(DELIMITER.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()))
        }

        @TypeConverter
        @JvmStatic
        fun toString(arrayList: ArrayList<String>): String {
            return arrayList.toString()
        }

        @TypeConverter
        @JvmStatic
        fun toStringAddressList(addresses: ArrayList<Address>): String {
            val addrezz: MutableList<String> = mutableListOf()
            for (address in addresses) {
                val gson = Gson()
                addrezz.add(gson.toJson(address))
            }
            val gson = Gson()
            return gson.toJson(addrezz)
        }

        @TypeConverter
        @JvmStatic
        fun toAddressList(addresses: String): ArrayList<Address> {
            val gson = Gson()
            val listType = object : TypeToken<ArrayList<String>>() {}.type
            val stringList = gson.fromJson<ArrayList<String>>(addresses, listType)

            val addrezz: MutableList<Address> = mutableListOf()
            for (addressString in stringList) {
                val addressItem = gson.fromJson(addressString, Address::class.java)
                addrezz.add(addressItem)
            }
            return ArrayList(addrezz)
        }


        @TypeConverter
        @JvmStatic
        fun toAddressString(address: Address): String {
            return Gson().toJson(address)
        }

        @TypeConverter
        @JvmStatic
        fun toAddress(addressString: String): Address {
            return Gson().fromJson(addressString, Address::class.java)
        }
    }
}