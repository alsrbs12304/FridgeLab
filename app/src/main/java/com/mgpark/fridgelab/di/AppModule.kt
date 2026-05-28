package com.mgpark.fridgelab.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // 앱 전역 의존성을 여기에 등록하세요. (Repository 바인딩은 RepositoryModule로 분리됨)
}
