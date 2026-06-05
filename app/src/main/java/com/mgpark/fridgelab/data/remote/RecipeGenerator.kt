package com.mgpark.fridgelab.data.remote

import com.google.firebase.ai.FirebaseAI
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import com.mgpark.fridgelab.data.dto.RecipeDto
import com.mgpark.fridgelab.data.mapper.toDomain
import com.mgpark.fridgelab.domain.model.Ingredient
import com.mgpark.fridgelab.domain.model.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

/** 보유 재료로 만들 수 있는 레시피를 Gemini로 생성한다(부족 재료·매칭 정보 포함). */
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

    suspend fun generate(ingredients: List<Ingredient>): List<Recipe> = withContext(Dispatchers.IO) {
        if (ingredients.isEmpty()) return@withContext emptyList()

        val have = ingredients.joinToString(", ") { ing ->
            if (ing.qty > 0) "${ing.name} ${ing.qty}${ing.unit}" else ing.name
        }

        val response = model.generateContent(
            content { text(buildPrompt(have)) }
        )
        val raw = response.text ?: return@withContext emptyList()
        json.decodeFromString<List<RecipeDto>>(raw)
            .filter { it.name.isNotBlank() }
            .mapIndexed { index, dto -> dto.toDomain(id = "recipe_$index") }
    }

    private companion object {
        fun buildPrompt(have: String): String =
            "내가 가진 재료로 만들 수 있는 한국 가정식 레시피를 6개 추천해주세요.\n" +
                "보유 재료: $have\n" +
                "각 레시피는 다음을 포함합니다: 요리 이름, 조리 시간(분), 난이도(쉬움/보통/어려움), " +
                "인분 수, 태그(예: 한식, 반찬), 한 줄 설명, 필요한 재료 목록, 조리 단계.\n" +
                "필요한 재료 각각에 대해, 내가 보유한 재료면 have=true, 추가로 사야 하면 have=false로 표시하세요. " +
                "보유 재료를 최대한 활용하는 레시피를 우선 추천하세요."

        val RECIPES_SCHEMA: Schema = Schema.array(
            items = Schema.obj(
                properties = mapOf(
                    "name" to Schema.string("요리 이름 (한국어)"),
                    "timeMin" to Schema.integer("조리 시간(분)"),
                    "level" to Schema.enumeration(listOf("쉬움", "보통", "어려움"), "난이도"),
                    "servings" to Schema.integer("인분 수"),
                    "tags" to Schema.array(items = Schema.string("태그 (예: 한식, 반찬)")),
                    "desc" to Schema.string("한 줄 설명"),
                    "need" to Schema.array(
                        items = Schema.obj(
                            properties = mapOf(
                                "name" to Schema.string("재료 이름"),
                                "amount" to Schema.string("필요한 양 (예: 1/2개, 200g, 약간)"),
                                "have" to Schema.boolean("보유 재료면 true, 사야 하면 false")
                            )
                        )
                    ),
                    "steps" to Schema.array(items = Schema.string("조리 단계 (한 단계씩)"))
                )
            )
        )
    }
}
