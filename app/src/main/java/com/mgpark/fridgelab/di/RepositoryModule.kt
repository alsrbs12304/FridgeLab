package com.mgpark.fridgelab.di

import com.mgpark.fridgelab.data.repository.AiIngredientRepository
import com.mgpark.fridgelab.data.repository.LlmRecipeRepository
import com.mgpark.fridgelab.domain.repository.IngredientRepository
import com.mgpark.fridgelab.domain.repository.RecipeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// 방식 A(LLM) <-> 방식 B(DB) 교체 시 아래 @Binds 한 줄만 바꿔주면 됩니다.
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindRecipeRepository(impl: LlmRecipeRepository): RecipeRepository

    @Binds
    @Singleton
    abstract fun bindIngredientRepository(impl: AiIngredientRepository): IngredientRepository
}
