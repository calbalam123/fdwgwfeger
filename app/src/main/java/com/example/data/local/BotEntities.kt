package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bot_config")
data class BotConfigEntity(
    @PrimaryKey val id: Int = 1,
    val botName: String = "아이브 DIVE 매니저",
    val botToken: String = "",
    val clientId: String = "123456789012345678",
    val guildId: String = "987654321098765432",
    val prefix: String = "!dive",
    val statusText: String = "🎧 IVE - '해야 (HEYA)' 스트리밍 중 ✨",
    val activityType: String = "LISTENING", // PLAYING, LISTENING, WATCHING
    val isRunning: Boolean = true,
    val isCloud247Enabled: Boolean = true,
    val hostingProvider: String = "AI Studio Cloud Runner (무중단 99.9%)",
    val geminiModel: String = "gemini-3.5-flash",
    val geminiPersona: String = "WONYOUNG_MIND", // WONYOUNG_MIND, LEADER_YUJIN, KONGSOONI_REI, DIVE_OFFICIAL
    val geminiSystemPrompt: String = "당신은 아이브(IVE) 팬 커뮤니티의 긍정 요정 '원영적 사고' 봇입니다. DIVE 팬들의 질문에 무한한 긍정과 사랑스러움, 그리고 럭키비키한 에너지로 친절하게 답해주세요. 아이브 멤버들의 최신 앨범, 컴백 소식, 응원법에 대해서도 잘 알고 있습니다. 말투는 상냥하고 밝게, 이모지를 가득 사용해주세요.",
    val geminiTemperature: Float = 0.7f,
    val antiRaid: Boolean = true,
    val antiSpam: Boolean = true,
    val slowmodeSec: Int = 3,
    val autoLogSecurity: Boolean = true,
    val uptimeStartTimestamp: Long = System.currentTimeMillis() - (1000 * 60 * 60 * 36), // 36 hours ago
    val totalMessagesProcessed: Long = 18420,
    val totalAiQueries: Long = 1256
)

@Entity(tableName = "tickets")
data class TicketEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ticketNumber: Int,
    val title: String,
    val category: String, // 팬아트/총공 인증, 이벤트 문의, 서버 신고, DIVE 등업 신청
    val authorName: String,
    val authorDiscordTag: String,
    val status: String, // OPEN, IN_PROGRESS, RESOLVED, CLOSED
    val priority: String, // URGENT, NORMAL, LOW
    val assignedStaff: String,
    val createdAt: Long = System.currentTimeMillis(),
    val latestMessage: String,
    val details: String
)

@Entity(tableName = "auto_notices")
data class AutoNoticeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val channelName: String,
    val embedColorHex: String = "#EC4899",
    val scheduleType: String, // DAILY_MORNING, YOUTUBE_CHECK, VOTE_REMINDER, CUSTOM
    val scheduleDisplayTime: String,
    val isEnabled: Boolean = true,
    val authorName: String = "IVE Official Notice Bot",
    val footerText: String = "DIVE와 영원히 함께할 IVE 커뮤니티 알림",
    val lastSentTimestamp: Long = 0L
)

@Entity(tableName = "role_rules")
data class RoleRuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val roleName: String,
    val roleColorHex: String,
    val ruleType: String, // REACTION, VERIFIED_DIVE, WELCOME_AUTO, CUSTOM
    val emoji: String,
    val targetChannel: String,
    val description: String,
    val assignedCount: Int = 0,
    val isEnabled: Boolean = true
)

@Entity(tableName = "bot_logs")
data class BotLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val level: String, // INFO, SUCCESS, WARN, SECURITY, AI, ERROR
    val tag: String,
    val message: String
)
