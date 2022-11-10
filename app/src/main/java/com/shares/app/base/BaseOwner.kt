package com.shares.app.base




interface BaseOwner {
    fun getBaseModel():BaseModel?
    fun navigateUp()
    fun navigate(id:Int)
}
