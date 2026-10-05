package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BotConfigEntity
import com.example.data.remote.DiscordHelper
import com.example.ui.BotViewModel
import com.example.ui.ChatMessage
import com.example.ui.components.SectionHeader
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
fun BotBuilderScreen(
    viewModel: BotViewModel,
    modifier: Modifier = Modifier
) {
    val configState by viewModel.config.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isChatLoading by viewModel.isChatLoading.collectAsState()
    val context = LocalContext.current

    val currentConfig = configState ?: BotConfigEntity()

    var botName by remember(currentConfig) { mutableStateOf(currentConfig.botName) }
    var prefix by remember(currentConfig) { mutableStateOf(currentConfig.prefix) }
    var statusText by remember(currentConfig) { mutableStateOf(currentConfig.statusText) }
    var botToken by remember(currentConfig) { mutableStateOf(currentConfig.botToken) }
    var clientId by remember(currentConfig) { mutableStateOf(currentConfig.clientId) }
    var isTokenVisible by remember { mutableStateOf(false) }
    var showStepGuide by remember { mutableStateOf(false) }

    // Gemini Settings
    var selectedPersona by remember(currentConfig) { mutableStateOf(currentConfig.geminiPersona) }
    var systemPrompt by remember(currentConfig) { mutableStateOf(currentConfig.geminiSystemPrompt) }
    var temperature by remember(currentConfig) { mutableFloatStateOf(currentConfig.geminiTemperature) }

    // Chat Simulator input
    var chatInput by remember { mutableStateOf("") }

    val inviteUrl = remember(clientId) {
        DiscordHelper.generateInviteUrl(clientId)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("bot_builder_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section: Discord Bot Basic Identity
        item {
            SectionHeader(
                title = "1. 디스코드 봇 기본 정보",
                subtitle = "코딩 없이 버튼 클릭으로 봇의 이름과 동작을 설정하세요"
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DiscordDarkSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DiscordDarkBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = botName,
                        onValueChange = { botName = it },
                        label = { Text("봇 이름 (Bot Name)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_bot_name"),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = prefix,
                            onValueChange = { prefix = it },
                            label = { Text("명령어 접두사") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_bot_prefix"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = statusText,
                            onValueChange = { statusText = it },
                            label = { Text("디스코드 상태 메시지") },
                            modifier = Modifier
                                .weight(2f)
                                .testTag("input_bot_status"),
                            singleLine = true
                        )
                    }
                }
            }
        }

        // Section: Discord API Integration & Token Setup
        item {
            SectionHeader(
                title = "2. 디스코드 연동 API 설정",
                subtitle = "Discord Developer Portal에서 발급받은 키를 안전하게 연동",
                actionText = if (showStepGuide) "가이드 닫기" else "연동 가이드 보기",
                onActionClick = { showStepGuide = !showStepGuide }
            )
        }

        // Step by step guide card
        item {
            AnimatedVisibility(visible = showStepGuide) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E212B)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DiscordBlurple.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "💡 3분 만에 끝나는 초간단 디스코드 연동법:",
                            color = DiscordBlurple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "1. discord.com/developers 에 접속하여 로그인합니다.\n" +
                                    "2. 'New Application' 클릭 후 앱을 만듭니다.\n" +
                                    "3. 좌측 'Bot' 메뉴 -> 'Reset Token'을 눌러 봇 토큰을 복사합니다.\n" +
                                    "4. Privileged Gateway Intents 항목의 3가지(Presence, Server Members, Message Content)를 모두 활성화(ON)합니다.\n" +
                                    "5. 아래 입력칸에 토큰과 Application ID를 넣으면 연동 완료!",
                            color = DiscordTextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DiscordDarkSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DiscordDarkBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Token Input with visibility toggle
                    OutlinedTextField(
                        value = botToken,
                        onValueChange = { botToken = it },
                        label = { Text("디스코드 봇 토큰 (Bot Token)") },
                        visualTransformation = if (isTokenVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isTokenVisible = !isTokenVisible }) {
                                Icon(
                                    imageVector = if (isTokenVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "토큰 표시 전환",
                                    tint = DiscordTextSecondary
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_bot_token"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = clientId,
                        onValueChange = { clientId = it },
                        label = { Text("클라이언트 ID (Application ID)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_client_id"),
                        singleLine = true
                    )

                    // Invite URL Box with copy button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DiscordDarkCard)
                            .padding(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("🔗 봇 초대 링크 (권한 비트마스크 자동 계산됨)", color = DiscordTextSecondary, fontSize = 11.sp)
                                Text(inviteUrl, color = IvePink, fontSize = 11.sp, maxLines = 1)
                            }
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Discord Bot Invite", inviteUrl))
                                    Toast.makeText(context, "초대 링크가 복사되었습니다!", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "초대링크 복사", tint = DiscordTextPrimary)
                            }
                        }
                    }
                }
            }
        }

        // Section: Gemini AI Chatbot Settings
        item {
            SectionHeader(
                title = "3. 제미나이(Gemini 3.5 Flash) 대화봇 설정",
                subtitle = "아이브 멤버 페르소나 및 지능형 대화 규칙 커스터마이징"
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DiscordDarkSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DiscordDarkBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "아이브 멤버 페르소나 템플릿 선택",
                        color = DiscordTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Persona Selection Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PersonaCard(
                            title = "원영적 사고🍀",
                            desc = "초긍정 럭키비키",
                            isSelected = selectedPersona == "WONYOUNG_MIND",
                            onClick = {
                                selectedPersona = "WONYOUNG_MIND"
                                systemPrompt = "당신은 아이브(IVE) 팬 커뮤니티의 긍정 요정 '원영적 사고' 봇입니다. DIVE 팬들의 질문에 무한한 긍정과 사랑스러움, 그리고 럭키비키한 에너지로 친절하게 답해주세요. 아이브 멤버들의 최신 앨범, 컴백 소식, 응원법에 대해서도 잘 알고 있습니다. 말투는 상냥하고 밝게, 이모지를 가득 사용해주세요."
                            },
                            modifier = Modifier.weight(1f)
                        )

                        PersonaCard(
                            title = "캡틴 유진🐶",
                            desc = "든든한 리더 모드",
                            isSelected = selectedPersona == "LEADER_YUJIN",
                            onClick = {
                                selectedPersona = "LEADER_YUJIN"
                                systemPrompt = "당신은 아이브(IVE)의 카리스마 리더 안유진 페르소나 봇입니다. 댕댕이처럼 활기차고 털털하며 든든하게 팬들을 챙겨줍니다. 무대 열정과 팬들에 대한 애정이 넘칩니다. 'DIVE~ 밥 챙겨먹었어?' 같은 따뜻한 격려를 건네주세요."
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PersonaCard(
                            title = "콩순이 레이🦋",
                            desc = "힙&큐트 랩퍼",
                            isSelected = selectedPersona == "KONGSOONI_REI",
                            onClick = {
                                selectedPersona = "KONGSOONI_REI"
                                systemPrompt = "당신은 아이브(IVE)의 나오이 레이 페르소나 봇입니다. 몽환적이고 독특한 감성과 귀여움, 재치 있는 말투가 특징입니다. 포근하고 따뜻한 어조로 DIVE와 대화해주세요."
                            },
                            modifier = Modifier.weight(1f)
                        )

                        PersonaCard(
                            title = "DIVE 매니저💎",
                            desc = "스케줄&응원법 전문",
                            isSelected = selectedPersona == "DIVE_OFFICIAL",
                            onClick = {
                                selectedPersona = "DIVE_OFFICIAL"
                                systemPrompt = "당신은 아이브(IVE) 디스코드 공식 커뮤니티 매니저 봇입니다. 공식 공지, 음원 스트리밍 총공 가이드, 콘서트 일정, 응원법을 정확하고 친절하게 안내합니다."
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // System Prompt Editor
                    OutlinedTextField(
                        value = systemPrompt,
                        onValueChange = { systemPrompt = it },
                        label = { Text("Gemini 시스템 지침 (System Instruction)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_system_prompt"),
                        maxLines = 5
                    )

                    // Temperature Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("대화 창의성 (Temperature)", color = DiscordTextSecondary, fontSize = 12.sp)
                            Text(String.format("%.1f", temperature), color = IvePink, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = temperature,
                            onValueChange = { temperature = it },
                            valueRange = 0.1f..1.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = IvePink,
                                activeTrackColor = IvePink
                            )
                        )
                    }

                    // Save Button
                    Button(
                        onClick = {
                            viewModel.updateBotConfig(
                                currentConfig.copy(
                                    botName = botName,
                                    prefix = prefix,
                                    statusText = statusText,
                                    botToken = botToken,
                                    clientId = clientId,
                                    geminiPersona = selectedPersona,
                                    geminiSystemPrompt = systemPrompt,
                                    geminiTemperature = temperature
                                )
                            )
                            Toast.makeText(context, "설정이 성공적으로 저장되었습니다!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DiscordBlurple),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("save_bot_config_button")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("설정 저장 및 디스코드 봇 반영", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section: Live Interactive Discord Chat Simulator
        item {
            SectionHeader(
                title = "4. 실시간 AI 대화 테스트 시뮬레이터",
                subtitle = "디스코드 채팅 환경에서 봇의 실시간 답변을 직접 테스트하세요"
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DiscordDarkSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DiscordDarkBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Chat message stream box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DiscordDarkCard)
                            .padding(10.dp)
                    ) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(chatMessages) { msg ->
                                DiscordChatMessageItem(message = msg)
                            }
                            if (isChatLoading) {
                                item {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(4.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            color = IvePink,
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "$botName 이(가) 생각 중입니다...",
                                            color = DiscordTextMuted,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Chat Input Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = chatInput,
                            onValueChange = { chatInput = it },
                            placeholder = { Text("!원영 오늘 힘든 일이 있었어...") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_simulator_input"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (chatInput.isNotBlank()) {
                                    viewModel.sendChatMessage(chatInput)
                                    chatInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = IvePink),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .height(52.dp)
                                .testTag("chat_simulator_send_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "전송")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PersonaCard(
    title: String,
    desc: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF381A3F) else DiscordDarkCard
        ),
        modifier = modifier
            .clickable(onClick = onClick)
            .border(
                1.5.dp,
                if (isSelected) IvePink else DiscordDarkBorder,
                RoundedCornerShape(10.dp)
            )
            .padding(10.dp)
    ) {
        Column {
            Text(
                text = title,
                color = if (isSelected) IvePink else DiscordTextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                color = DiscordTextSecondary,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun DiscordChatMessageItem(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isBot) Arrangement.Start else Arrangement.End
    ) {
        if (message.isBot) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(IvePink.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(message.avatarEmoji, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (message.isBot) Color(0xFF1E1F22) else DiscordBlurple)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Column {
                if (message.isBot) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = message.senderName,
                            color = IvePink,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(3.dp))
                                .background(DiscordBlurple)
                                .padding(horizontal = 3.dp, vertical = 1.dp)
                        ) {
                            Text("BOT", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                }
                Text(
                    text = message.text,
                    color = Color.White,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }
    }
}
