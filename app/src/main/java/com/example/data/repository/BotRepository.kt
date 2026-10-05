package com.example.data.repository

import com.example.data.local.AutoNoticeEntity
import com.example.data.local.BotConfigEntity
import com.example.data.local.BotDao
import com.example.data.local.BotLogEntity
import com.example.data.local.RoleRuleEntity
import com.example.data.local.TicketEntity
import com.example.data.remote.GeminiClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class BotRepository(private val botDao: BotDao) {

    val botConfig: Flow<BotConfigEntity?> = botDao.getBotConfig()
    val allTickets: Flow<List<TicketEntity>> = botDao.getAllTickets()
    val allNotices: Flow<List<AutoNoticeEntity>> = botDao.getAllNotices()
    val allRoleRules: Flow<List<RoleRuleEntity>> = botDao.getAllRoleRules()
    val recentLogs: Flow<List<BotLogEntity>> = botDao.getRecentLogs(100)

    suspend fun ensureInitialData() {
        val currentConfig = botDao.getBotConfig().firstOrNull()
        if (currentConfig == null) {
            // Seed initial Bot Config
            botDao.insertOrUpdateConfig(BotConfigEntity())

            // Seed initial Tickets
            val sampleTickets = listOf(
                TicketEntity(
                    ticketNumber = 101,
                    title = "멜론 음원 스트리밍 총공 인증 완료",
                    category = "팬아트/총공 인증",
                    authorName = "아이브사랑해#1201",
                    authorDiscordTag = "ive_love#1201",
                    status = "OPEN",
                    priority = "URGENT",
                    assignedStaff = "DIVE 총공팀장",
                    latestMessage = "오늘자 12:00 해야(HEYA) 스밍 캡처본 제출합니다. 등업 부탁드려요!",
                    details = "스트리밍 횟수 400회 달성 스크린샷 인증 확인 요청건"
                ),
                TicketEntity(
                    ticketNumber = 102,
                    title = "서버 내 스팸 유저 신고 및 슬로우모드 요청",
                    category = "서버 신고",
                    authorName = "원영바라기#0831",
                    authorDiscordTag = "wonyoung_fan#0831",
                    status = "IN_PROGRESS",
                    priority = "NORMAL",
                    assignedStaff = "보안관리자",
                    latestMessage = "자유채팅방에 도배글 작성하는 봇 계정 차단 검토 부탁드립니다.",
                    details = "외부 피싱 링크 게시 시도 감지됨 (Anti-Spam 쉴드 자동 포착)"
                ),
                TicketEntity(
                    ticketNumber = 103,
                    title = "아이브 공식 응원봉 맵핑 문의",
                    category = "이벤트 문의",
                    authorName = "콘서트가자#4492",
                    authorDiscordTag = "dive_concert#4492",
                    status = "RESOLVED",
                    priority = "LOW",
                    assignedStaff = "운영스태프",
                    latestMessage = "해결되었습니다! 안내해주신 앱 블루투스 연동으로 해결했어요.",
                    details = "팬미팅 현장 응원봉 페어링 부스 위치 및 안내"
                )
            )
            sampleTickets.forEach { botDao.insertTicket(it) }

            // Seed initial Auto Notices
            val sampleNotices = listOf(
                AutoNoticeEntity(
                    title = "☀️ DIVE 모닝 긍정 에너지 알림 (원영적 사고)",
                    content = "좋은 아침이에요 DIVE! 🍀 오늘 하루도 우리가 주인공인 멋진 하루가 될 거예요. 해야(HEYA)와 함께 힘찬 하루 시작해볼까요?",
                    channelName = "#공지사항",
                    embedColorHex = "#EC4899",
                    scheduleType = "DAILY_MORNING",
                    scheduleDisplayTime = "매일 오전 08:30"
                ),
                AutoNoticeEntity(
                    title = "📢 IVE 유튜브 자체 예능 '1,2,3 IVE' 업로드 알림",
                    content = "아이브 공식 유튜브에 새로운 에피소드가 공개되었습니다! 바로 시청하러 가기 🎬 댓글과 좋아요 잊지 마세요!",
                    channelName = "#유튜브-알림",
                    embedColorHex = "#8B5CF6",
                    scheduleType = "YOUTUBE_CHECK",
                    scheduleDisplayTime = "매주 월요일 오후 10:00"
                ),
                AutoNoticeEntity(
                    title = "🏆 이번 주 음악방송 사전 투표 독려",
                    content = "DIVE 여러분! 이번 주 음악방송 1위 후보에 올랐습니다. 엠카/뮤뱅/인가 실시간 및 사전 앱 투표 완료하고 인증 남겨주세요!",
                    channelName = "#음방-총공",
                    embedColorHex = "#F43F5E",
                    scheduleType = "VOTE_REMINDER",
                    scheduleDisplayTime = "실시간 연동"
                )
            )
            sampleNotices.forEach { botDao.insertNotice(it) }

            // Seed initial Role Rules
            val sampleRoles = listOf(
                RoleRuleEntity(
                    roleName = "💎 Official DIVE (인증 팬클럽)",
                    roleColorHex = "#EC4899",
                    ruleType = "VERIFIED_DIVE",
                    emoji = "💎",
                    targetChannel = "#팬인증-등업",
                    description = "아이브 공식 팬클럽 가입 증빙 및 퀴즈 통과 시 자동 지급",
                    assignedCount = 1420
                ),
                RoleRuleEntity(
                    roleName = "🐶 안댕댕단 (유진 최애)",
                    roleColorHex = "#3B82F6",
                    ruleType = "REACTION",
                    emoji = "🐶",
                    targetChannel = "#최애-역할-선택",
                    description = "안유진 리더를 응원하는 DIVE 전용 역할",
                    assignedCount = 680
                ),
                RoleRuleEntity(
                    roleName = "🌸 원영단 (원영 최애)",
                    roleColorHex = "#F43F5E",
                    ruleType = "REACTION",
                    emoji = "🌸",
                    targetChannel = "#최애-역할-선택",
                    description = "장원영을 응원하는 DIVE 전용 역할",
                    assignedCount = 890
                ),
                RoleRuleEntity(
                    roleName = "🦋 콩순이단 (레이 최애)",
                    roleColorHex = "#A855F7",
                    ruleType = "REACTION",
                    emoji = "🦋",
                    targetChannel = "#최애-역할-선택",
                    description = "나오이 레이를 응원하는 DIVE 전용 역할",
                    assignedCount = 540
                ),
                RoleRuleEntity(
                    roleName = "🐱 냥리즈단 (리즈 최애)",
                    roleColorHex = "#10B981",
                    ruleType = "REACTION",
                    emoji = "🐱",
                    targetChannel = "#최애-역할-선택",
                    description = "김리즈를 응원하는 DIVE 전용 역할",
                    assignedCount = 510
                ),
                RoleRuleEntity(
                    roleName = "🐯 가을선배단 (가을 최애)",
                    roleColorHex = "#F59E0B",
                    ruleType = "REACTION",
                    emoji = "🐯",
                    targetChannel = "#최애-역할-선택",
                    description = "김가을을 응원하는 DIVE 전용 역할",
                    assignedCount = 490
                ),
                RoleRuleEntity(
                    roleName = "🐥 막내이서단 (이서 최애)",
                    roleColorHex = "#EC4899",
                    ruleType = "REACTION",
                    emoji = "🐥",
                    targetChannel = "#최애-역할-선택",
                    description = "이현서를 응원하는 DIVE 전용 역할",
                    assignedCount = 480
                )
            )
            sampleRoles.forEach { botDao.insertRoleRule(it) }

            // Seed initial Logs
            val sampleLogs = listOf(
                BotLogEntity(level = "SUCCESS", tag = "CLOUD_RUNNER", message = "24/7 클라우드 호스팅 인스턴스 정상 가동 중 (Uptime 99.99%)"),
                BotLogEntity(level = "INFO", tag = "DISCORD_GATEWAY", message = "Gateway v10 연결 완료 - 웹소켓 하트비트 정상 (32ms)"),
                BotLogEntity(level = "AI", tag = "GEMINI_AI", message = "Gemini 3.5 Flash 모델 응답 생성 완료 (원영적 사고 모드)"),
                BotLogEntity(level = "SECURITY", tag = "SECURITY_SHIELD", message = "Anti-Raid 방화벽 감시 가동: 비정상 다중 계정 유입 없음"),
                BotLogEntity(level = "INFO", tag = "TICKET_SYS", message = "신규 티켓 #101 접수됨 (작성자: 아이브사랑해#1201)")
            )
            sampleLogs.forEach { botDao.insertLog(it) }
        }
    }

    suspend fun updateConfig(config: BotConfigEntity) {
        botDao.insertOrUpdateConfig(config)
    }

    suspend fun createTicket(
        title: String,
        category: String,
        author: String,
        details: String,
        priority: String = "NORMAL"
    ) {
        val count = botDao.getAllTickets().firstOrNull()?.size ?: 0
        val ticket = TicketEntity(
            ticketNumber = 100 + count + 1,
            title = title,
            category = category,
            authorName = author,
            authorDiscordTag = "${author.lowercase().replace(" ", "")}#${(1000..9999).random()}",
            status = "OPEN",
            priority = priority,
            assignedStaff = "대기중",
            latestMessage = details.take(60),
            details = details
        )
        botDao.insertTicket(ticket)
        botDao.insertLog(
            BotLogEntity(
                level = "INFO",
                tag = "TICKET_SYS",
                message = "티켓 #${ticket.ticketNumber} [${ticket.title}] 생성됨 (작성자: $author)"
            )
        )
    }

    suspend fun updateTicketStatus(ticket: TicketEntity, newStatus: String, assignedStaff: String? = null) {
        val updated = ticket.copy(
            status = newStatus,
            assignedStaff = assignedStaff ?: ticket.assignedStaff
        )
        botDao.updateTicket(updated)
        botDao.insertLog(
            BotLogEntity(
                level = "SUCCESS",
                tag = "TICKET_SYS",
                message = "티켓 #${ticket.ticketNumber} 상태 변경 -> $newStatus"
            )
        )
    }

    suspend fun deleteTicket(id: Long) {
        botDao.deleteTicketById(id)
    }

    suspend fun addNotice(
        title: String,
        content: String,
        channel: String,
        scheduleType: String,
        scheduleDisplay: String,
        embedColorHex: String
    ) {
        val notice = AutoNoticeEntity(
            title = title,
            content = content,
            channelName = channel,
            scheduleType = scheduleType,
            scheduleDisplayTime = scheduleDisplay,
            embedColorHex = embedColorHex
        )
        botDao.insertNotice(notice)
        botDao.insertLog(
            BotLogEntity(
                level = "SUCCESS",
                tag = "AUTO_NOTICE",
                message = "자동 공지사항 등록 완료: '$title' ($channel)"
            )
        )
    }

    suspend fun toggleNotice(notice: AutoNoticeEntity, enabled: Boolean) {
        botDao.updateNotice(notice.copy(isEnabled = enabled))
    }

    suspend fun deleteNotice(id: Long) {
        botDao.deleteNoticeById(id)
    }

    suspend fun addRoleRule(
        name: String,
        colorHex: String,
        emoji: String,
        ruleType: String,
        targetChannel: String,
        desc: String
    ) {
        val role = RoleRuleEntity(
            roleName = name,
            roleColorHex = colorHex,
            emoji = emoji,
            ruleType = ruleType,
            targetChannel = targetChannel,
            description = desc
        )
        botDao.insertRoleRule(role)
        botDao.insertLog(
            BotLogEntity(
                level = "SUCCESS",
                tag = "ROLE_MGR",
                message = "새로운 역할 규칙 생성: $emoji $name ($ruleType)"
            )
        )
    }

    suspend fun deleteRoleRule(id: Long) {
        botDao.deleteRoleRuleById(id)
    }

    suspend fun logEvent(level: String, tag: String, message: String) {
        botDao.insertLog(
            BotLogEntity(
                level = level,
                tag = tag,
                message = message
            )
        )
    }

    suspend fun clearAllLogs() {
        botDao.clearLogs()
        botDao.insertLog(
            BotLogEntity(
                level = "INFO",
                tag = "SYSTEM",
                message = "로그 내역이 초기화되었습니다."
            )
        )
    }

    suspend fun testGeminiChat(prompt: String, systemPrompt: String): String {
        val result = GeminiClient.queryGemini(
            prompt = prompt,
            systemInstruction = systemPrompt
        )
        val text = result.getOrElse { it.localizedMessage ?: "응답 실패" }
        botDao.insertLog(
            BotLogEntity(
                level = "AI",
                tag = "GEMINI_AI",
                message = "AI 질의: \"${prompt.take(30)}...\" -> 응답 생성 완료"
            )
        )
        return text
    }
}
