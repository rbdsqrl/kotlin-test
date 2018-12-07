package com.zopsmart.platformapplication.pojo

import java.io.Serializable

/**
 * Created by prateekhegde on 20/04/18.
 */

class OrderItem(
    var itemId: Long,
    var orderItemId: Long,
    var itemName: String?,
    var mrp: Double?,
    var discount: Double?,
    var orderedQty: Double?,
    var deliveredQty: Double?
) : Serializable {
    var sellingPrice: Double? = null

    val youPay: Double?
        get() = orderedQty!! * sellingPrice!!

    init {
        this.sellingPrice = mrp!! - discount!!
    }
}
