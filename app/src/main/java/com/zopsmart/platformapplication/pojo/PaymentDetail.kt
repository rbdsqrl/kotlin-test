package com.zopsmart.platformapplication.pojo

import com.zopsmart.platformapplication.PaymentType
import com.zopsmart.platformapplication.TransactionStatus
import java.io.Serializable

/**
 * Created by prateekhegde on 23/04/18.
 */

class PaymentDetail(
    var id: Long,
    var transactionCreatedAt: String,
    var transactionCompletedAt: String,
    var transactionStatus: TransactionStatus,
    var transactionType: PaymentType,
    var transactionId: String,
    var gatewayTransactionId: String,
    var bankTransactionId: String,
    var paymentServiceId: String,
    var amount: Double?
) : Serializable
