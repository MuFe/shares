package com.shares.app.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

class KeyboardUtil {
    fun hideInput(activity: Activity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // 隐藏软键盘
        imm.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
    }

    fun showInput(view:View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // 隐藏软键盘
        imm.showSoftInput(view, 0)
    }

    fun dpToPx(context: Context, dp:Int):Int{
        return (dp*context.resources.displayMetrics.density-0.5f).toInt()
    }

    fun dp2px(context: Context, dp: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    fun sp2px(context: Context, sp: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (sp * fontScale + 0.5f).toInt()
    }
}
