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
        private const val CHECK1 = "check1"
        private const val CHECK2 = "check2"
        private const val CHECK3 = "check3"
    }

    private val mSharedPreference =
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)


    fun getToken(): String {
        return mSharedPreference.getString(TOKEN_KEY,"").orEmpty()
    }



    fun getUserData(): String? {
        return mSharedPreference.getString(USERDATA_KEY,"")
    }

    fun clearLogin(){
        mSharedPreference.edit {
            putString(TOKEN_KEY, "")
            putInt(UID_KEY, 0)
            putBoolean(CHECK1,false)
            putBoolean(CHECK2,false)
            putBoolean(CHECK3,false)
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

    fun getCheck1():Boolean{
       return mSharedPreference.getBoolean(CHECK1,false)
    }
    fun getCheck2():Boolean{
        return mSharedPreference.getBoolean(CHECK2,false)
    }

    fun getCheck3():Boolean{
        return mSharedPreference.getBoolean(CHECK3,false)
    }

    fun setCheck1(show:Boolean){
        mSharedPreference.edit {
            putBoolean(CHECK1,show)
        }
    }
    fun setCheck2(show:Boolean){
        mSharedPreference.edit {
            putBoolean(CHECK2,show)
        }
    }
    fun setCheck3(show:Boolean){
        mSharedPreference.edit {
            putBoolean(CHECK3,show)
        }
    }
    fun isLogin(): Boolean {
        if (getToken().equals("")) {
            return false
        }
        return true
    }


}
