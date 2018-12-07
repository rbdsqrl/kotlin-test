package com.zopsmart.platformapplication.pojo

import com.zopsmart.platformapplication.DeliveryType
import com.zopsmart.platformapplication.PaymentStatus
import com.zopsmart.platformapplication.Status
import org.json.JSONObject

import java.io.Serializable
import java.util.ArrayList

/**
 * Created by anup on 03/04/18.
 */

class Order(
    var referenceNumber: String,
    var customerName: String,
    var status: Status,
    var paymentStatus: PaymentStatus,
    var paymentDetails: ArrayList<PaymentDetail>,
    var amount: Double?,
    var pendingAmount: Double?,
    var discount: Double?,
    var couponDiscount: Double?,
    var shippingCharges: Double?,
    var invoiceAmount: Double?,
    var amountPaid: Double?,
    var placedAt: String,
    var createdAt: String,
    var completedAt: String,
    var deliveryType: DeliveryType,
    var nItems: Int,
    var address: Address,
    var pickupAddress: PickupAddress,
    var orderItems: ArrayList<OrderItem>,
    data: JSONObject
) : Serializable {
    lateinit var result: String

    val data: JSONObject
        get() {
            return try {
                JSONObject(result)
            } catch (e: Exception) {
                JSONObject()
            }

        }

    init {
        try {
            this.result = data.toString()
        } catch (e: Exception) {
        }
    }
}
