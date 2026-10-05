package com.example.data.remote

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }

    suspend fun queryGemini(
        prompt: String,
        systemInstruction: String?,
        userApiKey: String? = null,
        temperature: Float = 0.7f
    ): Result<String> = withContext(Dispatchers.IO) {
        // Use user-provided API key or fallback to injected BuildConfig key
        val apiKey = when {
            !userApiKey.isNullOrBlank() -> userApiKey.trim()
            BuildConfig.GEMINI_API_KEY.isNotEmpty() && BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY" -> BuildConfig.GEMINI_API_KEY
            else -> ""
        }

        if (apiKey.isEmpty()) {
            // Provide simulated persona response when API key is not yet configured
            val simulated = generateSimulatedPersonaReply(prompt, systemInstruction)
            return@withContext Result.success(simulated)
        }

        try {
            val systemContent = if (!systemInstruction.isNullOrBlank()) {
                GeminiContent(parts = listOf(GeminiPart(text = systemInstruction)))
            } else null

            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        parts = listOf(GeminiPart(text = prompt)),
                        role = "user"
                    )
                ),
                systemInstruction = systemContent,
                generationConfig = GeminiGenerationConfig(
                    temperature = temperature,
                    maxOutputTokens = 1000
                )
            )

            val response = service.generateContent(apiKey = apiKey, request = request)
            val candidateText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!candidateText.isNullOrBlank()) {
                Result.success(candidateText.trim())
            } else if (response.error != null) {
                Result.failure(Exception("Gemini API Error: ${response.error.message} (${response.error.code})"))
            } else {
                Result.failure(Exception("응답 결과가 비어있습니다."))
            }
        } catch (e: Exception) {
            // If network or key error occurs, fallback gracefully with simulated response and error note
            val fallback = generateSimulatedPersonaReply(prompt, systemInstruction)
            Result.success("$fallback\n\n*(네트워크/API 키 안내: ${e.localizedMessage ?: "연결 확인 필요"})*")
        }
    }

    private fun generateSimulatedPersonaReply(prompt: String, systemInstruction: String?): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("원영") || lower.contains("럭키") || lower.contains("고민") || lower.contains("우울") ->
                "완전 럭키비키잖아!🍀 내가 응원해주려고 지금 이 메시지를 본 거 아니겠어? 지금 겪고 있는 일도 다 성장하기 위한 과정인 거 알지? 우리 DIVE는 존재 자체로 너무 소중하고 빛나니까 언제나 당당하게 웃어줘! 오늘 하루도 긍정의 마법 충전 완료!💖"

            lower.contains("유진") || lower.contains("리더") || lower.contains("화이팅") ->
                "안녕 DIVE! 아이브의 든든한 캡틴 유진이에요!🐶 오늘 하루도 수고 많았어요. 밥은 든든하게 챙겨 먹었죠? 언제나 우리 아이브 응원해줘서 고맙고, 우리 함께 끝까지 멋지게 달려봐요! 화이팅 넘치게 레츠고!"

            lower.contains("레이") || lower.contains("콩순") || lower.contains("귀여") ->
                "안녕하세요~ DIVE의 콩순이 레이예요🦋 오늘도 레이의 힙하고 귀여운 기운을 듬뿍 보내드릴게요. 날씨가 좋은 날엔 이어폰 끼고 우리 노래 들어주기 약속! 사랑해요 DIVE~ 포근한 하루 보내요✨"

            lower.contains("응원법") || lower.contains("해야") || lower.contains("heya") ->
                "📢 **[해야 (HEYA)] 공식 응원법 가이드!**\n" +
                "Intro: (함성) **안유진! 가을! 레이! 장원영! 리즈! 이서! 아.이.브!**\n" +
                "Chorus: '해야 해야 해야~' 부분에서 다 같이 손뼉 세 번 짝짝짝! 👏\n" +
                "음방 현장에서 DIVE의 함성이 가장 큰 힘이 돼요! 무대에서 만나요! ☀️"

            lower.contains("스케줄") || lower.contains("컴백") || lower.contains("콘서트") ->
                "📅 **아이브(IVE) 최신 스케줄 알림**\n" +
                "• 매주 목/금/토/일: 음악방송 실시간 투표 및 본방 사수\n" +
                "• 월드투어 'SHOW WHAT I HAVE' 앙코르 및 팬미팅 준비 중\n" +
                "• 유튜브 '1,2,3 IVE' 시즌 공개 알림 🔔"

            else ->
                "DIVE님, 반갑습니다! 🌟 저는 아이브 팬 커뮤니티 전용 Gemini AI 매니저 봇이에요. 궁금한 아이브 정보, 응원법, 스케줄을 물어보시거나 원영적 사고로 긍정 에너지를 충전해보세요! 무엇을 도와드릴까요? 💕"
        }
    }
}
