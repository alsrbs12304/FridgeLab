package com.mgpark.fridgelab.ui

import com.mgpark.fridgelab.domain.model.Category
import com.mgpark.fridgelab.domain.model.Freshness
import com.mgpark.fridgelab.domain.model.Ingredient
import com.mgpark.fridgelab.domain.model.Level
import com.mgpark.fridgelab.domain.model.NeededIngredient
import com.mgpark.fridgelab.domain.model.Recipe

/** 디자인 핸드오프(data.js)의 시드 데이터 — Compose Preview 및 AI 실패 시 폴백용. */
object SampleData {
    val ingredients: List<Ingredient> = listOf(
        Ingredient("onion", "양파", Category.VEG, 2, "개", Freshness.FRESH, 0.98f),
        Ingredient("carrot", "당근", Category.VEG, 2, "개", Freshness.FRESH, 0.96f),
        Ingredient("zucchini", "애호박", Category.VEG, 1, "개", Freshness.SOON, 0.91f),
        Ingredient("greenon", "대파", Category.VEG, 1, "단", Freshness.OK, 0.88f),
        Ingredient("pork", "돼지고기", Category.MEAT, 300, "g", Freshness.FRESH, 0.94f),
        Ingredient("shrimp", "새우", Category.MEAT, 12, "마리", Freshness.OK, 0.72f),
        Ingredient("egg", "계란", Category.DAIRY, 8, "개", Freshness.FRESH, 0.97f),
        Ingredient("milk", "우유", Category.DAIRY, 1, "팩", Freshness.SOON, 0.95f),
        Ingredient("cheese", "체다치즈", Category.DAIRY, 5, "장", Freshness.OK, 0.85f),
        Ingredient("butter", "버터", Category.DAIRY, 1, "개", Freshness.OK, 0.79f),
        Ingredient("apple", "사과", Category.FRUIT, 3, "개", Freshness.FRESH, 0.96f),
        Ingredient("tomato", "방울토마토", Category.FRUIT, 1, "팩", Freshness.OK, 0.83f),
        Ingredient("tofu", "두부", Category.ETC, 1, "모", Freshness.OK, 0.90f),
        Ingredient("kimchi", "김치", Category.ETC, 1, "통", Freshness.FRESH, 0.92f)
    )

    val recipes: List<Recipe> = listOf(
        Recipe(
            id = "kimchi-jjigae", name = "돼지고기 김치찌개", imageUrl = null,
            timeMin = 25, level = Level.EASY, servings = 2,
            tags = listOf("한식", "국·찌개"),
            desc = "잘 익은 김치와 돼지고기로 끓이는 든든한 한 끼.",
            need = listOf(
                NeededIngredient("김치", "1/4통", true),
                NeededIngredient("돼지고기", "200g", true),
                NeededIngredient("양파", "1/2개", true),
                NeededIngredient("대파", "1/2단", true),
                NeededIngredient("두부", "1/2모", true),
                NeededIngredient("고춧가루", "1큰술", false)
            ),
            steps = listOf(
                "돼지고기를 한입 크기로 썰고 김치는 먹기 좋게 썬다.",
                "냄비에 기름을 두르고 돼지고기를 볶다가 김치를 넣어 함께 볶는다.",
                "물 2컵을 붓고 고춧가루를 풀어 중불에서 10분간 끓인다.",
                "양파, 대파, 두부를 넣고 5분 더 끓여 마무리한다."
            )
        ),
        Recipe(
            id = "shrimp-zucchini", name = "새우 애호박 볶음", imageUrl = null,
            timeMin = 15, level = Level.EASY, servings = 2,
            tags = listOf("한식", "반찬"),
            desc = "아삭한 애호박과 탱탱한 새우의 산뜻한 볶음 요리.",
            need = listOf(
                NeededIngredient("새우", "10마리", true),
                NeededIngredient("애호박", "1개", true),
                NeededIngredient("양파", "1/2개", true),
                NeededIngredient("대파", "약간", true),
                NeededIngredient("굴소스", "1큰술", false)
            ),
            steps = listOf(
                "애호박은 반달썰기, 양파는 채썰고 새우는 손질한다.",
                "팬에 기름을 두르고 대파를 볶아 파기름을 낸다.",
                "새우를 넣어 익히다가 애호박과 양파를 넣고 센 불에 볶는다.",
                "굴소스로 간을 맞추고 살짝 더 볶아 완성한다."
            )
        ),
        Recipe(
            id = "egg-roll", name = "대파 계란말이", imageUrl = null,
            timeMin = 10, level = Level.EASY, servings = 2,
            tags = listOf("한식", "반찬"),
            desc = "폭신하게 말아낸 기본 반찬, 대파 향이 포인트.",
            need = listOf(
                NeededIngredient("계란", "4개", true),
                NeededIngredient("대파", "약간", true),
                NeededIngredient("당근", "1/4개", true),
                NeededIngredient("소금", "약간", false)
            ),
            steps = listOf(
                "계란을 풀고 잘게 썬 대파와 당근, 소금을 넣어 섞는다.",
                "약한 불의 팬에 계란물을 얇게 펴 붓는다.",
                "가장자리가 익으면 한쪽부터 돌돌 말아준다.",
                "남은 계란물을 부어가며 두툼하게 말아 완성한다."
            )
        ),
        Recipe(
            id = "cheese-toast", name = "체다 계란 토스트", imageUrl = null,
            timeMin = 12, level = Level.EASY, servings = 1,
            tags = listOf("브런치", "간단식"),
            desc = "버터에 구운 빵 위로 흐르는 치즈와 계란.",
            need = listOf(
                NeededIngredient("계란", "2개", true),
                NeededIngredient("체다치즈", "2장", true),
                NeededIngredient("버터", "1조각", true),
                NeededIngredient("우유", "2큰술", true),
                NeededIngredient("식빵", "2장", false)
            ),
            steps = listOf(
                "계란에 우유를 섞어 부드럽게 푼다.",
                "버터 두른 팬에 식빵을 노릇하게 굽는다.",
                "같은 팬에 계란을 스크램블하고 체다치즈를 올려 녹인다.",
                "구운 빵 사이에 계란과 치즈를 넣어 완성한다."
            )
        ),
        Recipe(
            id = "tofu-jorim", name = "두부조림", imageUrl = null,
            timeMin = 20, level = Level.MEDIUM, servings = 2,
            tags = listOf("한식", "반찬"),
            desc = "짭조름한 양념이 밴 부드러운 두부 반찬.",
            need = listOf(
                NeededIngredient("두부", "1모", true),
                NeededIngredient("대파", "1/2단", true),
                NeededIngredient("양파", "1/4개", true),
                NeededIngredient("간장", "2큰술", false),
                NeededIngredient("고춧가루", "1큰술", false)
            ),
            steps = listOf(
                "두부를 도톰하게 썰어 앞뒤로 노릇하게 굽는다.",
                "간장, 고춧가루, 다진 대파로 양념장을 만든다.",
                "구운 두부 위에 양념장을 얹고 물을 자작하게 붓는다.",
                "중불에서 국물이 졸아들 때까지 조려 완성한다."
            )
        ),
        Recipe(
            id = "apple-salad", name = "사과 당근 샐러드", imageUrl = null,
            timeMin = 10, level = Level.EASY, servings = 2,
            tags = listOf("샐러드", "비건"),
            desc = "아삭달콤한 사과와 당근의 상큼한 샐러드.",
            need = listOf(
                NeededIngredient("사과", "1개", true),
                NeededIngredient("당근", "1개", true),
                NeededIngredient("방울토마토", "1/2팩", true),
                NeededIngredient("올리브유", "1큰술", false)
            ),
            steps = listOf(
                "사과와 당근을 가늘게 채썬다.",
                "방울토마토는 반으로 자른다.",
                "올리브유와 소금, 후추로 드레싱을 만든다.",
                "재료를 모두 버무려 그릇에 담는다."
            )
        )
    )
}
