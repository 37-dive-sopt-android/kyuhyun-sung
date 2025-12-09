package com.sopt.dive.core.common.di

import android.content.Context
import com.sopt.dive.core.data.datasource.FakeAuthDataSource
import com.sopt.dive.core.data.repository.AuthRepositoryImpl
import com.sopt.dive.core.domain.repository.AuthRepository
import com.sopt.dive.core.domain.usecase.LoginUseCase

/**
 * ServiceLocator - 수동 의존성 주입 (Hilt 대체)
 * 
 * Service Locator 패턴이란?
 * - 객체 생성과 의존성 주입을 중앙에서 관리
 * - 싱글톤 객체를 제공
 * - Dagger/Hilt 없이 간단한 DI 구현
 */
object ServiceLocator {
    
    private var context: Context? = null
    
    private val fakeAuthDataSource: FakeAuthDataSource by lazy {
        FakeAuthDataSource()  // ✅ Fake 사용 중
    }
    
    private val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(dataSource = fakeAuthDataSource)
    }
    
    private val loginUseCase: LoginUseCase by lazy {
        LoginUseCase(authRepository = authRepository)
    }
    
    fun init(appContext: Context) {
        this.context = appContext.applicationContext
    }
    
    fun provideContext(): Context {
        return context ?: throw IllegalStateException("ServiceLocator not initialized")
    }
    
    fun provideAuthRepository(): AuthRepository = authRepository
    
    fun provideLoginUseCase(): LoginUseCase = loginUseCase
    
    fun provideFakeAuthDataSource(): FakeAuthDataSource = fakeAuthDataSource
    
    fun reset() {
        fakeAuthDataSource.reset()
    }
}
