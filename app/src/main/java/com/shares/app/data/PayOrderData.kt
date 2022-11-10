package com.shares.app.data


import com.shares.app.extension.toDateStr
import java.io.Serializable
import java.text.DecimalFormat

class PayOrderData(
    val orderId:String,
    var userId:String,
)