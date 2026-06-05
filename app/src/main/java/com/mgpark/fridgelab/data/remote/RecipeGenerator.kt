package com.mgpark.fridgelab.data.remote

import com.google.firebase.ai.FirebaseAI
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import com.mgpark.fridgelab.data.dto.RecipeDto
import com.mgpark.fridgelab.domain.model.Ingredient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

/** 재료 목록을 Gemini에 보내 레시피를 생성한다. */
class RecipeGenerator @Inject constructor(
    firebaseAI: FirebaseAI
) {
    private val json = Json { ignoreUnknownKeys = true }

    private val model = firebaseAI.generativeModel(
        modelName = "gemini-2.5-flash",
        generationConfig = generationConfig {
            responseMimeType = "application/json"
            responseSchema = RECIPES_SCHEMA
        }
    )

    suspend fun generate(ingredients: List<Ingredient>): List<RecipeDto> = withContext(Dispatchers.IO) {
        if (ingredients.isEmpty()) return@withContext emptyList()

        val ingredientText = ingredients.joinToString(", ") { ingredient ->
            ingredient.quantity?.let { "${ingredient.name}($it)" } ?: ingredient.name
        }

        val response = model.generateContent(
            content { text(buildPrompt(ingredientText)) }
        )
        val raw = response.text ?: return@withContext emptyList()
        json.decodeFromString<List<RecipeDto>>(raw)
    }

    private companion object {
        fun buildPrompt(ingredientText: String): String =
            "다음 재료로 만들 수 있는 한국 가정식 레시피를 3개 추천해주세요. " +
                "각 레시피는 요리 이름, 필요한 재료와 양, 조리 단계를 한국어로 포함해주세요. " +
                "주어진 재료를 최대한 활용하되, 소금·간장 같은 기본 양념은 추가해도 됩니다.\n" +
                "재료: $ingredientText"

        val RECIPES_SCHEMA: Schema = Schema.array(
            items = Schema.obj(
                properties = mapOf(
                    "title" to Schema.string("요리 이름 (한국어)"),
                    "ingredients" to Schema.array(
                        items = Schema.string("필요한 재료와 양 (예: 토마토 2개)")
                    ),
                    "steps" to Schema.array(
                        items = Schema.string("조리 단계 설명 (한 단계씩)")
                    )
                )
            )
        )
    }
}
