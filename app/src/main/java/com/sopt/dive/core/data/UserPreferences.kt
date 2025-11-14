package com.sopt.dive.core.data

import android.content.Context
import android.content.SharedPreferences

/**
 * 사용자 정보를 SharedPreferences에 저장하고 불러오는 클래스
 *
 * SharedPreferences는 Android에서 제공하는 키-값 쌍으로 데이터를 저장하는 간단한 저장소
 * 앱이 종료되어도 데이터가 유지되므로, 로그인 정보나 설정값을 저장하는 데 적합
 *
 * IntentKeys 대체.
 */
class UserPreferences private constructor(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val PREFS_NAME = "user_prefs"

        // 키 값들 업데이트
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_PW = "user_pw"
        private const val KEY_USER_NICKNAME = "user_nickname"
        private const val KEY_USER_EMAIL = "user_email"     // ✅ extra → email
        private const val KEY_USER_AGE = "user_age"         // ✅ age 추가
        private const val KEY_IS_LOGGED_IN = "is_logged_in"

        // Singleton 인스턴스
        @Volatile
        private var instance: UserPreferences? = null

        /**
         * UserPreferences의 싱글톤 인스턴스를 가져옵니다.
         *
         * Double-Checked Locking 패턴을 사용하여 멀티스레드 환경에서도
         * 안전하게 하나의 인스턴스만 생성되도록 보장합니다.
         */
        fun getInstance(context: Context): UserPreferences {
            return instance ?: synchronized(this) {
                instance ?: UserPreferences(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }

    /**
     * 회원가입 시 사용자 정보를 저장합니다.
     *
     */
    fun saveUser(userId: String, userPw: String, userNickname: String, userEmail: String, userAge: Int) {
        prefs.edit().apply {
            putString(KEY_USER_ID, userId)
            putString(KEY_USER_PW, userPw)
            putString(KEY_USER_NICKNAME, userNickname)
            putString(KEY_USER_EMAIL, userEmail)       // 이메일 저장
            putInt(KEY_USER_AGE, userAge)              // 나이 저장
            apply()
        }
    }
    /**
     * 로그인 시 입력한 ID와 비밀번호가 저장된 정보와 일치하는지 확인합니다.
     *
     * @return 로그인 성공 시 true, 실패 시 false
     */
    fun validateLogin(userId: String, userPw: String): Boolean {
        val savedId = prefs.getString(KEY_USER_ID, null)
        val savedPw = prefs.getString(KEY_USER_PW, null)

        return userId == savedId && userPw == savedPw
    }

    /**
     * 로그인 상태를 저장합니다.
     * 로그인에 성공하면 true로 설정하여, 다음에 앱을 열었을 때 자동 로그인되도록 합니다.
     */
    fun setLoggedIn(isLoggedIn: Boolean) {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
            apply()
        }
    }

    /**
     * 현재 로그인 상태를 확인합니다.
     *
     * @return 로그인되어 있으면 true, 아니면 false
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * 저장된 사용자 정보를 가져옵니다.
     *
     * @return 사용자 정보가 있으면 UserData 객체, 없으면 null
     */
    fun getUserData(): UserData? {
        val userId = prefs.getString(KEY_USER_ID, null)
        val userPw = prefs.getString(KEY_USER_PW, null)
        val userNickname = prefs.getString(KEY_USER_NICKNAME, null)
        val userEmail = prefs.getString(KEY_USER_EMAIL, null)    // 이메일 가져오기
        val userAge = prefs.getInt(KEY_USER_AGE, 0)              // 나이 가져오기

        return if (userId != null && userPw != null && userNickname != null && userEmail != null && userAge > 0) {
            UserData(userId, userPw, userNickname, userEmail, userAge)  // ✅ 새 구조
        } else {
            null
        }
    }

    /**
     * 로그아웃 시 저장된 로그인 상태를 초기화합니다.
     * 사용자 정보는 남겨두고 로그인 상태만 false로 변경합니다.
     */
    fun logout() {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, false)
            apply()
        }
    }

    /**
     * 모든 사용자 정보를 완전히 삭제합니다.
     * 회원 탈퇴나 앱 데이터 초기화 시 사용할 수 있습니다.
     */
    fun clearAll() {
        prefs.edit().clear().apply()
    }
}

/**
 * 사용자 데이터를 담는 데이터 클래스
 */
data class UserData(
    val userId: String,
    val userPw: String,
    val userNickname: String,
    val userEmail: String,    // extra → email
    val userAge: Int          // age 추가
)