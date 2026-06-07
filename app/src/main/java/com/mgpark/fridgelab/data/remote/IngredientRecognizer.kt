package com.mgpark.fridgelab.data.remote

import android.graphics.BitmapFactory
import android.util.Log
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

/** 냉장고 사진을 Gemini에 보내 재료를 인식한다(이름·카테고리·수량·단위·신뢰도). */
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
        val raw = response.text
        if (raw.isNullOrBlank()) {
            Log.w(TAG, "Gemini 응답이 비어있음 (text=null/blank)")
            return@withContext emptyList()
        }
        val parsed = json.decodeFromString<List<IngredientDto>>(raw).filter { it.name.isNotBlank() }
        Log.d(TAG, "인식 결과 ${parsed.size}개")
        parsed.mapIndexed { index, dto -> dto.toDomain(id = "ai_${index}_${dto.name}") }
    }

    private companion object {
        const val TAG = "FridgeLab"

        const val PROMPT =
            "이 냉장고/식료품 사진에 보이는 식재료를 모두 찾아주세요. " +
                "각 재료에 대해 이름(한국어), 카테고리(veg 채소 / meat 고기·해산물 / dairy 유제품·달걀 / fruit 과일 / etc 양념·기타), " +
                "수량(정수), 단위(개·g·단·팩·마리·모·통·장 등), 인식 신뢰도(0~1)를 알려주세요. " +
                "식재료가 아닌 물건은 제외합니다."

        val INGREDIENTS_SCHEMA: Schema = Schema.array(
            items = Schema.obj(
                properties = mapOf(
                    "name" to Schema.string("식재료 이름 (한국어)"),
                    "category" to Schema.enumeration(
                        listOf("veg", "meat", "dairy", "fruit", "etc"),
                        "카테고리"
                    ),
                    "qty" to Schema.integer("수량 (정수, 모르면 1)"),
                    "unit" to Schema.string("단위 (개, g, 단, 팩, 마리, 모, 통, 장 등)"),
                    "confidence" to Schema.double("인식 신뢰도 0~1")
                ),
                optionalProperties = listOf("qty", "unit", "confidence")
            )
        )
    }
}
