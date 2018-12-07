package com.zopsmart.platformapplication.repository

import android.content.Context
import com.zopsmart.platformapplication.pojo.Address
import com.zopsmart.platformapplication.BuildConfig
import com.zopsmart.platformapplication.exception.ZSException
import com.zopsmart.platformapplication.pojo.Customer
import org.json.JSONObject

class CustomerRepository(context: Context) : Repository(context) {
    //Customer keys
    companion object {
        const val USERNAME = "username"
        const val PASSWORD = "password"
        const val REMEMBER = "remember"
        const val PHONE = "phone"
        const val EMAIL = "email"
        const val ADDRESS = "address"
        const val LANDMARK = "landmark"
        const val PINCODE = "pincode"
        const val CITY = "city"
        const val LATITUDE = "latitude"
        const val LONGITUDE = "longitude"
        const val CUSTOMER_ID = "customerId"
        const val OLD_PASSWORD = "oldPassword"
        const val NEW_PASSWORD = "newPassword"
    }

    //Apis
    private val apiLogin = BuildConfig.host + "/api/login"
    private val apiRegister = BuildConfig.host + "/api/register"
    private var apiMyOrders = BuildConfig.host + "/api/order"
    private val apiAddress = BuildConfig.host + "/api/address"
    private val apiPassword = BuildConfig.host + "/api/password"
    private val apiLogout = BuildConfig.host + "/api/logout"

    //DAO
    private val dao: CustomerDao? = appDatabase?.getCustomerDao()

    fun insert(customer: Customer): Long {
        deleteAll()
        return dao!!.insert(customer)
    }

    fun getCustomer(): Customer {
        return dao!!.getUser()
    }

    private fun deleteAll() {
        dao?.deleteAll()
    }

    fun login(username: String, password: String): JSONObject? {
        val params = JSONObject()
        params.put(USERNAME, username)
        params.put(PASSWORD, password)
        params.put(REMEMBER, "true")

        val zsRequest = ZSRequest(Method.POST, apiLogin, null, params)
        return zsRequest.fetch()
    }

    fun register(name: String, phone: String, email: String, address: String, landmark: String, pincode: String, city: String, latitude: Double, longitude: Double): JSONObject {
        val params = JSONObject()
        params.put(NAME, name)
        params.put(PHONE, phone)
        params.put(EMAIL, email)
        params.put(ADDRESS, address)
        params.put(CITY, city)
        params.put(PINCODE, pincode)
        params.put(LANDMARK, landmark)
        params.put(LATITUDE, latitude)
        params.put(LONGITUDE, longitude)

        val zsRequest = ZSRequest(Method.POST, apiRegister, null, params)
        return zsRequest.fetch()
    }

    fun loadOrders(): JSONObject {
        val zsRequest = ZSRequest(Repository.Method.GET, apiMyOrders, null, null)
        return zsRequest.fetch()
    }

    fun loadOrder(orderRefNo: String): JSONObject {
        val zsRequest = ZSRequest(Repository.Method.GET, "$apiMyOrders/$orderRefNo", null, null)
        return zsRequest.fetch()
    }


    fun changeDefaultAddress(address: Address) {
        val customer = getCustomer()
        customer.defaultAddress = address
        insert(customer)
    }

    fun deleteAddressInDB(id: Long) {
        val customer = getCustomer()
        val addressList = customer.addresses
        val newAddressList = mutableListOf<Address>()
        for(address in addressList) {
            if(address.id != id)
                newAddressList.add(address)
        }
        customer.addresses = ArrayList(newAddressList)
        //TODO (if the size is 0, does the default address need to change)
        if(newAddressList.size !=0)
            customer.defaultAddress = newAddressList[0]
        insert(customer)
    }

    fun editAddressInDB(editedAddress: Address) {
        val customer = getCustomer()
        val addressList: MutableList<Address> = customer.addresses
        //Find the index to replace the list
        var index = -1
        for(i in 0..addressList.size)
            if(addressList[i].id == editedAddress.id) {
                index = i
                break
            }

        if(index == -1)
            return
        addressList[index] = editedAddress
        customer.addresses = ArrayList(addressList)
        insert(customer)
    }

    fun addNewAddressToDB(address: Address) {
        val customer = getCustomer()
        val addressList: MutableList<Address> = customer.addresses
        addressList.add(address)
        customer.addresses = ArrayList(addressList)
        insert(customer)
    }

    fun getAddressParams(address: String, landmark: String, pincode: String, city: String, latitude: Double, longitude: Double): JSONObject {
        val params = JSONObject()
        params.put(ADDRESS, address)
        params.put(LANDMARK, landmark)
        params.put(PINCODE, pincode)
        params.put(CITY, city)
        params.put(LATITUDE, latitude)
        params.put(LONGITUDE, longitude)
        return params
    }

    fun addNewAddress(address: String, landmark: String, pincode: String, city: String, latitude: Double, longitude: Double): JSONObject {
        val params = getAddressParams(address, landmark, pincode, city, latitude, longitude)
        val zsRequest = ZSRequest(Repository.Method.POST, apiAddress, null, params)
        return zsRequest.fetch()
    }

    fun editAddress(addressID: Long, address: String, landmark: String, pincode: String, city: String, latitude: Double, longitude: Double): JSONObject {
        val params = getAddressParams(address, landmark, pincode, city, latitude, longitude)
        val zsRequest = ZSRequest(Repository.Method.PUT, "$apiAddress/$addressID", null, params)
        return zsRequest.fetch()
    }

    fun deleteAddress(id: Long?): JSONObject {
        val zsRequest = ZSRequest(Repository.Method.DELETE, "$apiAddress/$id", null, null)
        return zsRequest.fetch()
    }

    fun changePassword(customerId: Long, oldPassword: String, newPassword: String?, confirmNewPassword: String?): JSONObject {
        if(newPassword == null || newPassword != confirmNewPassword)
            throw ZSException("Passwords do not match. Please try again")
        val params = JSONObject()
        params.put(OLD_PASSWORD, oldPassword)
        params.put(NEW_PASSWORD, newPassword)
        params.put(CUSTOMER_ID, customerId)
        val zsRequest = ZSRequest(Method.POST, apiPassword, null, params)
        return zsRequest.fetch()
    }

    fun forgotPassword(emailPhone: String?): JSONObject {
        if(emailPhone == null)
            throw ZSException("Field cannot be empty")
        val params = JSONObject()
        params.put(USERNAME, emailPhone)

        val zsRequest = ZSRequest(Method.POST, apiPassword, null, params)
        return zsRequest.fetch()
    }

    @Throws(Exception::class)
    fun logout(): JSONObject {
        val zsRequest = ZSRequest(Repository.Method.GET, apiLogout, null, null)
        return zsRequest.fetch()
    }

}