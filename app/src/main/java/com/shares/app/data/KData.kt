package com.shares.app.data

import com.github.wangyiqian.stockchart.entities.FLAG_DEFAULT
import com.github.wangyiqian.stockchart.entities.FLAG_EMPTY
import com.github.wangyiqian.stockchart.entities.IKEntity
import com.github.wangyiqian.stockchart.entities.KEntity


class KData(
    private var high: Float,
    private var low: Float,
    private var open: Float,
    private var close: Float,
    private var volume: Long,
    private var dataMills: Long,
    private var avgPrice: Float? = null,
    private var flag: Int = FLAG_DEFAULT
):IKEntity {
    override fun getHighPrice() = high

    override fun setHighPrice(price: Float) {
        this.high = price
    }

    override fun getLowPrice() = low

    override fun setLowPrice(price: Float) {
        this.low = price
    }

    override fun getOpenPrice() = open

    override fun setOpenPrice(price: Float) {
        this.open = price
    }

    override fun getClosePrice() = close

    override fun setClosePrice(price: Float) {
        this.close = price
    }

    override fun getVolume() = volume

    override fun setVolume(volume: Long) {
        this.volume = volume
    }

    override fun getTime() = dataMills

    override fun setTime(time: Long) {
        this.dataMills = time
    }

    override fun getAvgPrice() = avgPrice

    override fun setAvgPrice(price: Float?) {
        this.avgPrice = price
    }

    override fun setFlag(flag: Int) {
        this.flag = flag
    }

    override fun getFlag(): Int {
        return flag
    }

}