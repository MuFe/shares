package com.shares.app.util

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shares.app.data.UserData
import java.lang.Exception


class PreferenceUtil(val context: Context) {
    companion object {
        private const val PREFERENCE_NAME = "Dana"
        private const val TOKEN_KEY = "token"
        private const val UID_KEY = "uid"
        private const val USERDATA_KEY = "userData"
        private const val FLOW_KEY = "flow"
        private const val URL_KEY = "url"
        private const val CANCEL_KEY = "cancel"
        private const val EDITEXPAN_KEY = "editExpan"
    }

    private val mSharedPreference =
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)


    fun getToken(): String {
        return mSharedPreference.getString(TOKEN_KEY,"").orEmpty()
    }

    fun getUid(): Int {
        return mSharedPreference.getInt(UID_KEY,0)
    }

    fun getUserData(): String? {
        return mSharedPreference.getString(USERDATA_KEY,"")
    }

    fun clearLogin(){
        mSharedPreference.edit {
            putString(TOKEN_KEY, "")
            putInt(UID_KEY, 0)
            putBoolean(EDITEXPAN_KEY, false)
            putBoolean(EDITEXPAN_KEY,false)
            putBoolean(FLOW_KEY,false)
        }
    }

    fun setToken(token: String, token_type: String) {
        mSharedPreference.edit {
            putString(TOKEN_KEY, token_type +" "+ token)
        }
    }

    fun setUserName(name: String){
        mSharedPreference.edit {
            putString(USERDATA_KEY,name)
        }
    }

    fun getFlow():Boolean{
       return mSharedPreference.getBoolean(FLOW_KEY,false)
    }

    fun setFlow(show:Boolean){
        mSharedPreference.edit {
            putBoolean(FLOW_KEY,show)
        }
    }
    fun isLogin(): Boolean {
        if (getToken().equals("")) {
            return false
        }
        return true
    }


}
