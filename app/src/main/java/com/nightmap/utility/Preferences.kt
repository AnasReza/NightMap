package com.nightmap.utility

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log

class Preferences(context: Context) {

    private val FIRST_CHECK = "first_check"
    private val USER_ID = "user_id"
    private val BAR_ID = "bar_id"
    private val BAR_STATUS = "bar_status"
    private val BAR_PASSWORD = "bar_password"
    private val ADMIN_ID = "admin_id"
    private val ADMIN_PASSWORD = "admin_password"
    private val USER_TYPE = "user_type"
    private val USER_NAME = "user_name"
    private val LOGIN = "login"
    private val DEEP_LINK="deep_link"
    private val SHARING_EVENT_ID="sharing_event_id"


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
    fun getAdminID(): String? {
        var deviceToken = preferences.getString(ADMIN_ID, "")
        return deviceToken
    }

    fun setAdminID(value: String) {
        preferences.edit().putString(ADMIN_ID, value).apply()
    }
    fun getAdminPassword(): String? {
        var deviceToken = preferences.getString(ADMIN_PASSWORD, "")
        return deviceToken
    }

    fun setAdminPassword(value: String) {
        preferences.edit().putString(ADMIN_PASSWORD, value).apply()
    }
    fun getBarPassword(): String? {
        var deviceToken = preferences.getString(BAR_PASSWORD, "")
        return deviceToken
    }

    fun setBarPassword(value: String) {
        preferences.edit().putString(BAR_PASSWORD, value).apply()
    }

    fun getBarID(): String? {
        var deviceToken = preferences.getString(BAR_ID, "")
        return deviceToken
    }

    fun setBarID(value: String) {
        preferences.edit().putString(BAR_ID, value).apply()
    }
    fun getBarStatus(): String? {
        var deviceToken = preferences.getString(BAR_STATUS, "pending")
        return deviceToken
    }

    fun setBarStatus(value: String) {
        preferences.edit().putString(BAR_STATUS, value).apply()
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
    fun getDeepLink(): String? {
        var deviceToken = preferences.getString(DEEP_LINK, "")
        return deviceToken
    }

    fun setDeepLink(value: String) {
        preferences.edit().putString(DEEP_LINK, value).apply()
    }
    fun getSharingEventId(): String? {
        var deviceToken = preferences.getString(SHARING_EVENT_ID, "")
        return deviceToken
    }

    fun setSharingEventId(value: String) {
        preferences.edit().putString(SHARING_EVENT_ID, value).apply()
    }

}