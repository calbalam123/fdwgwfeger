package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BotLogEntity
import com.example.ui.BotViewModel
import com.example.ui.NavigationTab
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.DiscordBlurple
import com.example.ui.theme.DiscordDarkBorder
import com.example.ui.theme.DiscordDarkCard
import com.example.ui.theme.DiscordDarkSurface
import com.example.ui.theme.DiscordTextMuted
import com.example.ui.theme.DiscordTextPrimary
import com.example.ui.theme.DiscordTextSecondary
import com.example.ui.theme.IveGold
import com.example.ui.theme.IvePink
import com.example.ui.theme.IveRose
import com.example.ui.theme.IveViolet
import com.example.ui.theme.StatusOnline

@Composable
fun DashboardScreen(
    viewModel: BotViewModel,
    modifier: Modifier = Modifier
) {
    val config by viewModel.config.collectAsState()
    val tickets by viewModel.tickets.collectAsState()
    val notices by viewModel.notices.collectAsState()
    val logs by viewModel.logs.collectAsState()
    val ping by viewModel.serverPing.collectAsState()
    val uptime by viewModel.uptimeSeconds.collectAsState()

    var showQuickNoticeDialog by remember { mutableStateOf(false) }

    val openTicketsCount = tickets.count { it.status == "OPEN" || it.status == "IN_PROGRESS" }

    // Format uptime into human readable text
    val hours = uptime / 3600
    val minutes = (uptime % 3600) / 60
    val uptimeString = "${hours}시간 ${minutes}분 가동 중"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header Card with Bot Power & Status
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DiscordDarkSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        Brush.horizontalGradient(listOf(IvePink.copy(alpha = 0.6f), DiscordBlurple.copy(alpha = 0.6f))),
                        RoundedCornerShape(18.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(listOf(IvePink, IveViolet))
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🤖", fontSize = 24.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = config?.botName ?: "아이브 DIVE 매니저",
                                    color = DiscordTextPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "접두사: ${config?.prefix ?: "!dive"}  •  샤드 #0",
                                    color = DiscordTextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        StatusBadge(
                            status = if (config?.isRunning == true) "ONLINE" else "OFFLINE",
                            modifier = Modifier.testTag("bot_status_badge")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Status Message Strip
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(DiscordDarkCard)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🎧", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = config?.statusText ?: "🎧 IVE - '해야 (HEYA)' 스트리밍 중 ✨",
                                color = DiscordTextSecondary,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick Power and Restart buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.toggleBotRunning(!(config?.isRunning ?: true)) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (config?.isRunning == true) StatusOnline else DiscordBlurple
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("toggle_power_button")
                        ) {
                            Icon(Icons.Default.PowerSettingsNew, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (config?.isRunning == true) "가동 중지" else "봇 시작",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = { viewModel.restartBot() },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DiscordTextPrimary),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("restart_bot_button")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("재접속/리로드", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // IVE Fanroom Special Feature Highlight Banner
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF23142B)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, IvePink.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .clickable { viewModel.selectTab(NavigationTab.IVE_FANROOM) }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(IvePink.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🌸", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "IVE 팬방 올인원 전용 매니저 모드",
                            color = IvePink,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "원영적 사고 AI 대화 • 응원법 안내 • DIVE 등업 퀴즈 연동",
                            color = DiscordTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    Text(
                        text = "열기 >",
                        color = IvePink,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Key Statistics Grid (2x2 Cards)
        item {
            SectionHeader(title = "실시간 서버 통계 현황")
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "서버 멤버",
                    value = "2,840명",
                    subtext = "오늘 +42명 입장",
                    icon = Icons.Default.People,
                    accentColor = DiscordBlurple,
                    modifier = Modifier.weight(1f),
                    testTag = "stat_members"
                )
                StatCard(
                    title = "대기 티켓",
                    value = "${openTicketsCount}건",
                    subtext = "총 ${tickets.size}건 중 처리중",
                    icon = Icons.Default.ConfirmationNumber,
                    accentColor = IvePink,
                    modifier = Modifier.weight(1f),
                    testTag = "stat_tickets"
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "AI 대화 처리",
                    value = "${config?.totalAiQueries ?: 1256}회",
                    subtext = "Gemini 3.5 Flash",
                    icon = Icons.Default.AutoAwesome,
                    accentColor = IveViolet,
                    modifier = Modifier.weight(1f),
                    testTag = "stat_ai"
                )
                StatCard(
                    title = "24시 호스팅",
                    value = "${ping}ms",
                    subtext = uptimeString,
                    icon = Icons.Default.Speed,
                    accentColor = StatusOnline,
                    modifier = Modifier.weight(1f),
                    testTag = "stat_uptime"
                )
            }
        }

        // Quick Action Bar
        item {
            SectionHeader(
                title = "빠른 관리 작업",
                subtitle = "클릭 한 번으로 채널 공지 및 시스템 제어"
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DiscordDarkCard),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showQuickNoticeDialog = true }
                        .border(1.dp, DiscordDarkBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.Campaign, contentDescription = null, tint = IvePink, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("빠른 공지 전송", color = DiscordTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("디스코드 웹훅", color = DiscordTextMuted, fontSize = 10.sp)
                    }
                }

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DiscordDarkCard),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.selectTab(NavigationTab.BOT_BUILDER) }
                        .border(1.dp, DiscordDarkBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.Forum, contentDescription = null, tint = IveViolet, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("AI 대화 시뮬레이터", color = DiscordTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("원영적 사고 모드", color = DiscordTextMuted, fontSize = 10.sp)
                    }
                }

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DiscordDarkCard),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.selectTab(NavigationTab.HOSTING_LOGS) }
                        .border(1.dp, DiscordDarkBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = StatusOnline, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("보안 & 24시 클라우드", color = DiscordTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("안티 레이드 쉴드", color = DiscordTextMuted, fontSize = 10.sp)
                    }
                }
            }
        }

        // Live Event Activity Stream
        item {
            SectionHeader(
                title = "실시간 이벤트 활동",
                subtitle = "최근 봇 활동 및 이벤트 내역",
                actionText = "전체 로그 >",
                onActionClick = { viewModel.selectTab(NavigationTab.HOSTING_LOGS) }
            )
        }

        items(logs.take(4)) { log ->
            LogItemRow(log = log)
        }
    }

    // Quick Notice Sender Dialog
    if (showQuickNoticeDialog) {
        var noticeTitle by remember { mutableStateOf("📢 [긴급공지] 오늘 밤 12시 스밍 총공 안내") }
        var noticeContent by remember { mutableStateOf("DIVE 여러분! 오늘 밤 12시 멜론/지니 음원 총공이 진행됩니다. 공식 가이드라인을 확인해주세요!") }
        var targetChannel by remember { mutableStateOf("#공지사항") }

        AlertDialog(
            onDismissRequest = { showQuickNoticeDialog = false },
            title = { Text("디스코드 빠른 공지 전송", color = DiscordTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("지정된 디스코드 채널로 예쁜 임베드 형태의 공지를 즉시 전송합니다.", color = DiscordTextSecondary, fontSize = 12.sp)
                    OutlinedTextField(
                        value = targetChannel,
                        onValueChange = { targetChannel = it },
                        label = { Text("전송 대상 채널") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = noticeTitle,
                        onValueChange = { noticeTitle = it },
                        label = { Text("공지 제목") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = noticeContent,
                        onValueChange = { noticeContent = it },
                        label = { Text("공지 내용") },
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addNotice(
                            title = noticeTitle,
                            content = noticeContent,
                            channel = targetChannel,
                            scheduleType = "INSTANT",
                            scheduleDisplay = "즉시 발송",
                            colorHex = "#EC4899"
                        )
                        showQuickNoticeDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IvePink)
                ) {
                    Text("공지 발송", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showQuickNoticeDialog = false }) {
                    Text("취소", color = DiscordTextSecondary)
                }
            },
            containerColor = DiscordDarkSurface
        )
    }
}

@Composable
fun LogItemRow(log: BotLogEntity) {
    val levelColor = when (log.level) {
        "SUCCESS" -> StatusOnline
        "WARN" -> IveGold
        "ERROR" -> IveRose
        "SECURITY" -> Color(0xFF38BDF8)
        "AI" -> IveViolet
        else -> DiscordBlurple
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(DiscordDarkCard)
            .border(1.dp, DiscordDarkBorder.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(levelColor)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "[${log.tag}]",
                    color = levelColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = log.level,
                    color = DiscordTextMuted,
                    fontSize = 10.sp
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = log.message,
                color = DiscordTextPrimary,
                fontSize = 12.sp,
                maxLines = 2
            )
        }
    }
}
