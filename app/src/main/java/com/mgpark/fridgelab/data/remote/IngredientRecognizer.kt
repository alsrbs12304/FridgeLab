package com.mgpark.fridgelab.data.remote

import android.graphics.BitmapFactory
import com.google.firebase.ai.FirebaseAI
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import com.mgpark.fridgelab.data.dto.IngredientDto
import com.mgpark.fridgelab.data.mapper.toDomain
import com.mgpark.fridgelab.domain.model.Ingredient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

/** 냉장고 사진을 Gemini에 보내 식재료 목록을 인식한다. */
class IngredientRecognizer @Inject constructor(
    firebaseAI: FirebaseAI
) {
    private val json = Json { ignoreUnknownKeys = true }

    private val model = firebaseAI.generativeModel(
        modelName = "gemini-2.5-flash",
        generationConfig = generationConfig {
            responseMimeType = "application/json"
            responseSchema = INGREDIENTS_SCHEMA
        }
    )

    suspend fun recognize(image: ByteArray): List<Ingredient> = withContext(Dispatchers.IO) {
        val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
            ?: return@withContext emptyList()

        val response = model.generateContent(
            content {
                image(bitmap)
                text(PROMPT)
            }
        )
        val raw = response.text ?: return@withContext emptyList()
        json.decodeFromString<List<IngredientDto>>(raw).map { it.toDomain() }
    }

    private companion object {
        const val PROMPT =
            "이 사진(냉장고 또는 식료품)에 보이는 식재료를 모두 찾아주세요. " +
                "각 재료의 이름과 가능하면 수량을 한국어로 알려주세요. " +
                "식재료가 아닌 물건은 제외합니다."

        val INGREDIENTS_SCHEMA: Schema = Schema.array(
            items = Schema.obj(
                properties = mapOf(
                    "name" to Schema.string("식재료 이름 (한국어)"),
                    "quantity" to Schema.string("수량이나 양 (예: 2개, 약간). 알 수 없으면 생략")
                ),
                optionalProperties = listOf("quantity")
            )
        )
    }
}
