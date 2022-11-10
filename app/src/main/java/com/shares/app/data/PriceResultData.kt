package com.shares.app.data


import com.shares.app.extension.toDateStr
import java.io.Serializable
import java.text.DecimalFormat

class PriceResultData(
    val yesterday:PriceData,
    val current:PriceData,
)