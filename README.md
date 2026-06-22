# 🧊 FridgeLab — 냉장고 재료 인식 & AI 레시피 추천 앱

냉장고 내부를 촬영하면 **AI가 식재료를 자동 인식**하고, 보유 재료로 만들 수 있는 **레시피를 매칭률 기반으로 추천**하는 Android 앱입니다.

![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202026.02-4285F4?logo=jetpackcompose&logoColor=white)
![Gemini](https://img.shields.io/badge/Gemini%202.5%20Flash-Firebase%20AI-FF6F00?logo=googlegemini&logoColor=white)
![Hilt](https://img.shields.io/badge/Hilt-2.59-34A853?logo=android&logoColor=white)
![minSdk](https://img.shields.io/badge/minSdk-26-3DDC84?logo=android&logoColor=white)

---

## ✨ 핵심 기능

- 📷 **냉장고 촬영** — CameraX 프리뷰/촬영, 플래시·전후면 전환·갤러리 불러오기, 촬영 가이드 오버레이
- 🤖 **AI 재료 인식** — 사진을 Gemini로 분석해 **재료명·카테고리·수량·단위·신뢰도**를 구조화 추출
- 🥗 **재료 확인·편집** — 카테고리별 분류, 신선도/수량 편집, 선택, 저신뢰 재료 "AI 확인 필요" 표시, 직접 추가
- 🍳 **AI 레시피 추천** — 보유 재료로 만들 수 있는 레시피 생성, **매칭률(보유/필요) 계산**, 매칭률·조리시간·난이도순 정렬
- 📖 **레시피 상세** — 재료 체크리스트(보유/부족·장보기), 번호 조리 순서, 북마크

---

## 🛠 기술 스택

| 분류 | 기술 |
|------|------|
| **언어 / 빌드** | Kotlin 2.2.10, AGP 9.2.1, Gradle Version Catalog, KSP |
| **SDK** | compileSdk/targetSdk 36, minSdk 26, Java 11 |
| **UI** | Jetpack Compose (BOM 2026.02.01), Material 3 |
| **AI** | Firebase AI Logic — Gemini 2.5 Flash, App Check (Play Integrity) |
| **카메라** | CameraX 1.4.1 |
| **DI** | Hilt 2.59.2 + hilt-navigation-compose |
| **비동기 / 상태** | Coroutines 1.9.0, Flow / StateFlow |
| **네비게이션** | Navigation Compose 2.8.0 |
| **직렬화** | kotlinx.serialization 1.7.0 |
| **기타** | Coil, Pretendard(폰트 번들), material-icons-extended |

---

## 🏗 아키텍처

**Clean Architecture (3-Layer) + 단방향 데이터 흐름(UDF)**

```
┌─────────────────── presentation (ui) ───────────────────┐
│  Compose Screens ──events(콜백)──▶ FridgeSessionViewModel │
│        ▲────────────state(StateFlow)────────────┘        │
└──────────────────────────│──────────────────────────────┘
                           │ UseCase 호출
┌──────────────────────────▼──────── domain ──────────────┐
│   UseCase ──▶ Repository(interface) · Model(순수 Kotlin) │  ← 프레임워크 비의존
└──────────────────────────▲──────────────────────────────┘
                           │ 구현(의존성 역전, @Binds)
┌──────────────────────────│──────── data ────────────────┐
│  RepositoryImpl ──▶ Remote(Gemini) ──▶ DTO ──▶ Mapper    │
└──────────────────────────────────────────────────────────┘
```

- **의존성 역전(DIP)** — `domain`은 인터페이스만 정의하고 `data`가 구현 → 도메인이 프레임워크에 비의존
- **데이터소스 교체 용이** — `RepositoryModule`의 `@Binds` 한 줄로 *AI 방식 ↔ DB 방식* 교체 가능
- **단일 상태 저장소(SSOT)** — 화면 공유 상태를 **NavGraph 범위로 스코핑한 `FridgeSessionViewModel`** 에 두어, 화면을 오가도 편집 상태 보존

### 패키지 구조
```
com.mgpark.fridgelab
├── di/            # AppModule, AiModule, RepositoryModule (Hilt)
├── domain/        # model · repository(interface) · usecase   ← 순수 Kotlin
├── data/          # remote(Gemini) · dto · mapper · repository(impl)
├── navigation/    # NavHost · Route · FridgeSessionViewModel · sharedViewModel 헬퍼
└── ui/            # camera · ingredients · recipes · components · theme
```

---

## 🔍 기술적으로 신경 쓴 점

1. **LLM 구조화 출력 안정화** — `responseMimeType=application/json` + `responseSchema`(`Schema.array/obj/enumeration`)로 출력 형식을 강제하고 `kotlinx.serialization`으로 파싱. 인식기/생성기가 서로 다른 스키마를 쓰도록 `FirebaseAI`를 주입받아 용도별 모델을 각각 구성.
2. **AI 무료 할당량(Quota) 3단계 방어** — ① 선택 재료 서명 기반 **중복 호출 방지**, ② `retryDelay` 파싱 **자동 재시도(백오프)**, ③ retryDelay 없는 하드 한도는 **즉시 안내**(헛대기 제거) + 실패 사유 가시화.
3. **카메라 UX** — 촬영 후 라이브 프리뷰 흔들림을 막기 위해 **촬영 프레임을 정지 이미지로 고정**(회전 보정)해 분석 애니메이션을 정지 화면 위에서 실행.
4. **Compose 디테일** — `Canvas`+`Path`(2차 베지어)로 둥근 ㄱ자 가이드, 무한 회전 애니메이션, 커스텀 진행 바, `FlowRow`/가로 스크롤로 텍스트 오버플로우 해결, `LazyListState` 자동 스크롤.
5. **디자인 핸드오프 정밀 재현** — High-fidelity 디자인의 `oklch` 색을 HEX 토큰으로 변환, `CompositionLocal`(FridgeColors)로 Material3에 없는 커스텀 디자인 토큰까지 테마화.
