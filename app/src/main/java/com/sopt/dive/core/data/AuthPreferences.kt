package com.sopt.dive.core.data

import android.content.Context
import android.content.SharedPreferences

/**
 * 로그인 상태만 관리하는 간단한 클래스
 * 사용자 정보는 모두 API에서 가져옴
 */
class AuthPreferences private constructor(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "auth_prefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"

        @Volatile
        private var instance: AuthPreferences? = null

        fun getInstance(context: Context): AuthPreferences {
            return instance ?: synchronized(this) {
                instance ?: AuthPreferences(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }

    /**
     * 로그인 성공 시 필요한 정보만 저장
     */
    fun saveLoginInfo(userId: Int, username: String, password: String) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_USERNAME, username)
            .putString(KEY_PASSWORD, password)
            .apply()
    }

    /**
     * 로그인 상태 확인
     */
    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    /**
     * 자동 로그인용 정보 가져오기
     */
    fun getLoginInfo(): LoginInfo? {
        return if (isLoggedIn()) {
            LoginInfo(
                userId = prefs.getInt(KEY_USER_ID, 0),
                username = prefs.getString(KEY_USERNAME, "") ?: "",
                password = prefs.getString(KEY_PASSWORD, "") ?: ""
            )
        } else null
    }

    /**
     * 로그아웃
     */
    fun logout() {
        prefs.edit().clear().apply()
    }
}

data class LoginInfo(
    val userId: Int,
    val username: String,
    val password: String
)