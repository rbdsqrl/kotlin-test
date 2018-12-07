package com.zopsmart.platformapplication.repository

import android.content.Context
import com.zopsmart.platformapplication.BuildConfig
import com.zopsmart.platformapplication.extension.toParams
import com.zopsmart.platformapplication.pojo.CartItem
import org.json.JSONObject

class CartItemRepository(context: Context) : Repository(context) {
    //Cart keys
    companion object {
        const val keyCouponCode = "couponCode"
        const val keyDiscount = "discount"
        const val keyCoupon = "coupon"
        const val keyOrderAmount = "orderAmount"
        const val keyTime = "time"
        const val keyQ = "q"
        const val keyCart = "cart"
    }

    //Apis for cart items
    private val apiSyncCart = BuildConfig.host + "/cart.json"
    private val apiApplyCoupon = BuildConfig.host + "/api/apply-coupon"

    private val cartItemDao = appDatabase?.getCartItemDao()
    private val cartItems = cartItemDao?.getAllCartItemsLiveData()

    //Repository functions
    //TODO check how to do these functions asynchronously
    fun insert(cartItem: CartItem) {
        cartItemDao?.insert(cartItem)
    }

    fun delete(cartItem: CartItem) {
        cartItemDao?.delete(cartItem)
    }

    fun getCartPrice() = cartItemDao?.getCartPrice()

    fun getCartDiscount() = cartItemDao?.getCartDiscount()

    fun getFinalPrice() = cartItemDao?.getFinalPrice()

    fun getNoOfCartItems() = cartItemDao?.getNoOfCartItems()

    fun getCartItemLiveData() = cartItems

    fun getCartItems() = cartItemDao?.getAllCartItems()

    fun getCartItemWithId(id: Long) = cartItemDao?.getCartItemWithId(id)

    fun incrementItemQuantity(cartItem: CartItem) {
        cartItem.quantity += 1
        insert(cartItem)
    }

    fun decrementItemQuantity(cartItem: CartItem) {
        val cartQuantity = cartItem.quantity - 1
        setCartQuantity(cartItem, cartQuantity)
    }

    fun setCartQuantity(cartItem: CartItem, quantity: Int) {
        if (quantity <= 0) {
            delete(cartItem)
            return
        }
        cartItem.quantity = quantity
        insert(cartItem)
    }

    fun clearCart() {
        cartItemDao?.clearCart()
    }

    fun getCartParams(lastSyncedTime: String?): JSONObject {
        val items = getCartItems()
        //if last updated time is null add default value as 1 and return

        if (lastSyncedTime == null)
            return JSONObject().put(keyTime, 1)

        return items.toParams(lastSyncedTime.toLong())
    }

    fun syncCartItems(): JSONObject? {
        if (LocalStorage.init(context).get(LocalStorage.ACCESS_TOKEN, null as String?) == null)
            return null
        val currentTime: Long = System.currentTimeMillis() / 1000
        val items = getCartItems()
        val params =items.toParams(currentTime)
        val syncParams = JSONObject()
        syncParams.put(keyCart, params.toString())
        val zsRequest = ZSRequest(Method.PUT, apiSyncCart, null, syncParams)
        return zsRequest.fetch()
    }

    fun applyCoupon(couponCode: String, totalAmount: Double): JSONObject? {
        val zsRequest = ZSRequest(Method.GET, apiApplyCoupon, null, null)
        zsRequest.addQueryParameter(keyCouponCode, couponCode)
        zsRequest.addQueryParameter(keyOrderAmount, "$totalAmount")
        return zsRequest.fetch()
    }
}
