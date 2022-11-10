package com.shares.app.ui

import android.os.Bundle

interface FragmentHost {
    fun reload()
    fun reload(bundle: Bundle?)
}
