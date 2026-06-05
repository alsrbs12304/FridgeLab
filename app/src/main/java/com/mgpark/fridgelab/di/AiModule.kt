package com.mgpark.fridgelab.di

import com.google.firebase.Firebase
import com.google.firebase.ai.FirebaseAI
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiModule {
    // FirebaseAI 인스턴스를 제공한다. 각 remote(재료 인식/레시피 생성)는
    // 용도에 맞는 responseSchema로 GenerativeModel을 직접 구성한다.
    @Provides
    @Singleton
    fun provideFirebaseAI(): FirebaseAI =
        Firebase.ai(backend = GenerativeBackend.googleAI())
}
