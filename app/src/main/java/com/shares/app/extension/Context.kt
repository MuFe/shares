package com.shares.app.extension

import android.R
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.PermissionChecker

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.toastLong(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.toast(@StringRes messageRes: Int) {
    Toast.makeText(this, messageRes, Toast.LENGTH_SHORT).show()
}

fun Context.toastLong(@StringRes messageRes: Int) {
    Toast.makeText(this, messageRes, Toast.LENGTH_LONG).show()
}

fun Context.checkPermissions(permission: String): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (R.attr.targetSdkVersion >= Build.VERSION_CODES.M) {
            return this.checkSelfPermission(permission)== PackageManager.PERMISSION_GRANTED
        } else {
            return PermissionChecker.checkSelfPermission(this, permission)== PermissionChecker.PERMISSION_GRANTED
        }
    } else {
        return true
    }
}