// PreferencesUtil.kt
package com.example.remeeton.model.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesUtil(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_CURRENT_USER_ID = "current_user_id"
    }

    var currentUserId: String?
        get() = sharedPreferences.getString(KEY_CURRENT_USER_ID, null)
        set(value) {
            sharedPreferences.edit().putString(KEY_CURRENT_USER_ID, value).apply()
        }
}
