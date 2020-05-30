package com.nightmap.utility

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log

class Preferences(context: Context) {

    private val FIRST_CHECK = "first_check"
    private val USER_ID = "user_id"
    private val BAR_ID = "bar_id"
    private val USER_TYPE = "user_type"
    private val USER_NAME = "user_name"
    private val LOGIN = "login"


    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun getFirstCheck(): Boolean? {
        var deviceToken = preferences.getBoolean(FIRST_CHECK, false)
        return deviceToken
    }

    fun setFirstCheck(value: Boolean) {
        preferences.edit().putBoolean(FIRST_CHECK, value).apply()
    }

    fun getUserID(): String? {
        var deviceToken = preferences.getString(USER_ID, "")
        return deviceToken
    }

    fun setUserID(value: String) {
        preferences.edit().putString(USER_ID, value).apply()
    }

    fun getBarID(): String? {
        var deviceToken = preferences.getString(BAR_ID, "")
        return deviceToken
    }

    fun setBarID(value: String) {
        preferences.edit().putString(BAR_ID, value).apply()
    }

    fun getUserType(): String? {
        var deviceToken = preferences.getString(USER_TYPE, "")
        return deviceToken
    }

    fun setUserType(value: String) {
        preferences.edit().putString(USER_TYPE, value).apply()
    }

    fun getUserName(): String? {
        var deviceToken = preferences.getString(USER_NAME, "")
        return deviceToken
    }

    fun setUserName(value: String) {
        preferences.edit().putString(USER_NAME, value).apply()
    }

    fun getLogin(): Boolean? {
        var deviceToken = preferences.getBoolean(LOGIN, false)
        return deviceToken
    }

    fun setLogin(value: Boolean) {
        preferences.edit().putBoolean(LOGIN, value).apply()
    }

}