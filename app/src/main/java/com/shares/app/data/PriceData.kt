package com.shares.app.data


import com.shares.app.extension.toDateStr
import java.io.Serializable
import java.text.DecimalFormat

class PriceData(
    val createTime:String,
    var price:Float,
    var open:Float,
    var maxPrice:Float,
    val minPrice:Float,
)