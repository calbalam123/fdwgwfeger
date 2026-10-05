package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AutoNoticeEntity
import com.example.data.local.BotConfigEntity
import com.example.data.local.BotLogEntity
import com.example.data.local.RoleRuleEntity
import com.example.data.local.TicketEntity
import com.example.data.repository.BotRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val senderName: String,
    val isBot: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val avatarEmoji: String = "✨"
)

enum class NavigationTab(val title: String, val iconTag: String) {
    DASHBOARD("대시보드", "dashboard"),
    BOT_BUILDER("AI 봇 빌더", "bot_builder"),
    IVE_FANROOM("아이브 팬룸", "ive_fanroom"),
    TICKETS_ROLES("티켓·역할", "tickets_roles"),
    HOSTING_LOGS("클라우드·로그", "hosting_logs")
}

class BotViewModel(private val repository: BotRepository) : ViewModel() {

    val config: StateFlow<BotConfigEntity?> = repository.botConfig
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val tickets: StateFlow<List<TicketEntity>> = repository.allTickets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notices: StateFlow<List<AutoNoticeEntity>> = repository.allNotices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val roleRules: StateFlow<List<RoleRuleEntity>> = repository.allRoleRules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val logs: StateFlow<List<BotLogEntity>> = repository.recentLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentTab = MutableStateFlow(NavigationTab.DASHBOARD)
    val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

    private val _selectedTicketFilter = MutableStateFlow("ALL")
    val selectedTicketFilter: StateFlow<String> = _selectedTicketFilter.asStateFlow()

    // Interactive Discord Bot Chat Simulator
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                senderName = "아이브 DIVE 매니저",
                isBot = true,
                text = "반가워요 DIVE! 🌟 제미나이 AI 기반 아이브 팬방 매니저 봇이에요.\n명령어 예시:\n• !원영 오늘 힘든 일이 있었어\n• !응원법 해야\n• !스케줄 이번 주 음방 일정 알려줘\n무엇이든 물어보세요! 💖",
                avatarEmoji = "🤖"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    // 24/7 Hosting Health Metrics
    private val _uptimeSeconds = MutableStateFlow(129600L) // 36 hours default
    val uptimeSeconds: StateFlow<Long> = _uptimeSeconds.asStateFlow()

    private val _serverPing = MutableStateFlow(32)
    val serverPing: StateFlow<Int> = _serverPing.asStateFlow()

    private val _serverCpu = MutableStateFlow(4.2f)
    val serverCpu: StateFlow<Float> = _serverCpu.asStateFlow()

    private val _serverRam = MutableStateFlow(142)
    val serverRam: StateFlow<Int> = _serverRam.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureInitialData()
        }

        // Real-time server telemetry simulation loop
        viewModelScope.launch {
            while (isActive) {
                delay(1000)
                _uptimeSeconds.value += 1
                if (_uptimeSeconds.value % 5 == 0L) {
                    _serverPing.value = (28..39).random()
                    _serverCpu.value = 3.5f + (0..15).random() / 10f
                    _serverRam.value = 138 + (0..12).random()
                }
            }
        }
    }

    fun selectTab(tab: NavigationTab) {
        _currentTab.value = tab
    }

    fun setTicketFilter(filter: String) {
        _selectedTicketFilter.value = filter
    }

    fun updateBotConfig(updated: BotConfigEntity) {
        viewModelScope.launch {
            repository.updateConfig(updated)
            repository.logEvent("INFO", "CONFIG", "봇 환경설정이 성공적으로 저장되었습니다.")
        }
    }

    fun toggleBotRunning(isRunning: Boolean) {
        val current = config.value ?: BotConfigEntity()
        viewModelScope.launch {
            repository.updateConfig(current.copy(isRunning = isRunning))
            val status = if (isRunning) "온라인 (ONLINE)" else "오프라인 (OFFLINE)"
            repository.logEvent(
                if (isRunning) "SUCCESS" else "WARN",
                "POWER",
                "봇 가동 상태 전환: $status"
            )
        }
    }

    fun toggleCloud247(enabled: Boolean) {
        val current = config.value ?: BotConfigEntity()
        viewModelScope.launch {
            repository.updateConfig(current.copy(isCloud247Enabled = enabled))
            repository.logEvent(
                if (enabled) "SUCCESS" else "WARN",
                "CLOUD_RUNNER",
                if (enabled) "24시간 무중단 클라우드 호스팅 워커 가동 시작" else "24시간 호스팅 일시 중지됨"
            )
        }
    }

    fun restartBot() {
        viewModelScope.launch {
            repository.logEvent("WARN", "RESTART", "디스코드 봇 핫 리로드 및 게이트웨이 재접속 시도...")
            delay(800)
            repository.logEvent("SUCCESS", "RESTART", "게이트웨이 v10 재접속 성공! 모든 샤드(Shard 0/1) 정상 온라인.")
        }
    }

    fun sendChatMessage(userText: String) {
        if (userText.isBlank()) return
        val userMsg = ChatMessage(
            senderName = "DIVE 유저",
            isBot = false,
            text = userText,
            avatarEmoji = "👤"
        )
        _chatMessages.value = _chatMessages.value + userMsg
        _isChatLoading.value = true

        viewModelScope.launch {
            val botConfig = config.value ?: BotConfigEntity()
            val replyText = repository.testGeminiChat(
                prompt = userText,
                systemPrompt = botConfig.geminiSystemPrompt
            )
            _isChatLoading.value = false
            val botMsg = ChatMessage(
                senderName = botConfig.botName,
                isBot = true,
                text = replyText,
                avatarEmoji = "✨"
            )
            _chatMessages.value = _chatMessages.value + botMsg
        }
    }

    fun createTicket(title: String, category: String, author: String, details: String, priority: String) {
        viewModelScope.launch {
            repository.createTicket(title, category, author, details, priority)
        }
    }

    fun updateTicketStatus(ticket: TicketEntity, newStatus: String, staff: String? = null) {
        viewModelScope.launch {
            repository.updateTicketStatus(ticket, newStatus, staff)
        }
    }

    fun deleteTicket(id: Long) {
        viewModelScope.launch {
            repository.deleteTicket(id)
        }
    }

    fun addNotice(title: String, content: String, channel: String, scheduleType: String, scheduleDisplay: String, colorHex: String) {
        viewModelScope.launch {
            repository.addNotice(title, content, channel, scheduleType, scheduleDisplay, colorHex)
        }
    }

    fun toggleNotice(notice: AutoNoticeEntity, enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleNotice(notice, enabled)
        }
    }

    fun deleteNotice(id: Long) {
        viewModelScope.launch {
            repository.deleteNotice(id)
        }
    }

    fun addRoleRule(name: String, colorHex: String, emoji: String, ruleType: String, targetChannel: String, desc: String) {
        viewModelScope.launch {
            repository.addRoleRule(name, colorHex, emoji, ruleType, targetChannel, desc)
        }
    }

    fun deleteRoleRule(id: Long) {
        viewModelScope.launch {
            repository.deleteRoleRule(id)
        }
    }

    fun clearAllLogs() {
        viewModelScope.launch {
            repository.clearAllLogs()
        }
    }

    fun sendInstantNoticeTest(notice: AutoNoticeEntity) {
        viewModelScope.launch {
            repository.logEvent(
                "SUCCESS",
                "WEBHOOK",
                "디스코드 [${notice.channelName}] 채널로 임베드 공지 발송 완료: \"${notice.title}\""
            )
        }
    }
}

class BotViewModelFactory(private val repository: BotRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BotViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BotViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
