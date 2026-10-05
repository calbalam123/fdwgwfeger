package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.BotViewModel
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

data class IveMember(
    val name: String,
    val birthDate: String,
    val position: String,
    val emoji: String,
    val color: Color,
    val command: String,
    val quote: String
)

val IVE_MEMBERS = listOf(
    IveMember("안유진", "2003.09.01", "리더 / 보컬", "🐶", Color(0xFF3B82F6), "!유진", "DIVE와 함께라면 언제나 당당하고 멋지게!"),
    IveMember("가을", "2002.09.24", "메인댄서 / 랩", "🐯", Color(0xFFF59E0B), "!가을", "가을선배의 힙한 댄스 브레이크 볼 사람!"),
    IveMember("레이", "2004.02.03", "랩 / 보컬", "🦋", Color(0xFFA855F7), "!레이", "DIVE 마음속으로 쏙 들어갈 콩순이예요~"),
    IveMember("장원영", "2004.08.31", "보컬", "🌸", Color(0xFFEC4899), "!원영", "완전 럭키비키잖아🍀 오늘도 긍정 파워 100%!"),
    IveMember("리즈", "2004.11.21", "메인보컬", "🐱", Color(0xFF10B981), "!리즈", "맑고 청량한 리즈의 음색으로 힐링하세요!"),
    IveMember("이서", "2007.02.21", "보컬 / 댄서", "🐥", Color(0xFFF43F5E), "!이서", "막내 이서의 비타민 에너지 레츠고!")
)

data class FanchantSong(
    val title: String,
    val album: String,
    val lyricsSnippet: String,
    val fanResponse: String
)

val IVE_SONGS = listOf(
    FanchantSong(
        title = "해야 (HEYA)",
        album = "IVE SWITCH (2024)",
        lyricsSnippet = "해야 해야 해야~ 한 입에 널 삼켜버릴 테야",
        fanResponse = "(짝짝짝!) 안유진! 가을! 레이! 장원영! 리즈! 이서! 아.이.브! ☀️"
    ),
    FanchantSong(
        title = "Accendio",
        album = "IVE SWITCH (2024)",
        lyricsSnippet = "Watch me don't touch me, Accendio!",
        fanResponse = "아.센.디.오! 빛나는 마법에 걸린 DIVE! 🪄"
    ),
    FanchantSong(
        title = "I AM",
        album = "I've IVE (2023)",
        lyricsSnippet = "That's my life is 아름다운 갤럭시, Be a writer 인생이란 소설의",
        fanResponse = "네가 가는 길이 꽃길이야! 아이브 최고! 🌌"
    ),
    FanchantSong(
        title = "After LIKE",
        album = "After LIKE (2022)",
        lyricsSnippet = "What's after 'LIKE'? You-ooh and I-i-i, It's more than 'LIKE'",
        fanResponse = "아이브! 다이브! 사랑해! (떼창) 💖"
    )
)

@Composable
fun IveFanroomScreen(
    viewModel: BotViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedMember by remember { mutableStateOf(IVE_MEMBERS[3]) } // Default to Wonyoung
    var selectedSong by remember { mutableStateOf(IVE_SONGS[0]) }

    // DIVE Certification Quiz interactive state
    var quizStep by remember { mutableIntStateOf(0) }
    var quizAnswerInput by remember { mutableStateOf("") }
    var quizCompleted by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("ive_fanroom_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section: IVE 6 Members Selector & Profiles
        item {
            SectionHeader(
                title = "1. 아이브(IVE) 멤버별 전용 커맨드 & 프로필",
                subtitle = "멤버별 단독 봇 명령어(!원영, !유진)와 최애 역할을 설정하세요"
            )
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(IVE_MEMBERS) { member ->
                    val isSelected = member.name == selectedMember.name
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) member.color.copy(alpha = 0.2f) else DiscordDarkCard
                        ),
                        modifier = Modifier
                            .clickable { selectedMember = member }
                            .border(
                                1.5.dp,
                                if (isSelected) member.color else DiscordDarkBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(member.emoji, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = member.name,
                                    color = if (isSelected) member.color else DiscordTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(member.command, color = DiscordTextMuted, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Selected Member Detailed Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DiscordDarkSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, selectedMember.color.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(selectedMember.color.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(selectedMember.emoji, fontSize = 22.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "${selectedMember.name} (${selectedMember.position})",
                                    color = DiscordTextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "생일: ${selectedMember.birthDate}  •  디스코드 명령어: ${selectedMember.command}",
                                    color = DiscordTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Member representative quote
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DiscordDarkCard)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "\"${selectedMember.quote}\"",
                            color = selectedMember.color,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Button to register/test command
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                viewModel.sendChatMessage("${selectedMember.command} 오늘 기분 좋은 소식 알려줘!")
                                Toast.makeText(context, "${selectedMember.command} 명령어가 AI 대화창에 전송되었습니다!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = selectedMember.color),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("${selectedMember.command} 호출 테스트", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.addRoleRule(
                                    name = "${selectedMember.emoji} ${selectedMember.name}단",
                                    colorHex = "#EC4899",
                                    emoji = selectedMember.emoji,
                                    ruleType = "REACTION",
                                    targetChannel = "#최애-역할-선택",
                                    desc = "${selectedMember.name}을(를) 응원하는 DIVE 전용 역할"
                                )
                                Toast.makeText(context, "${selectedMember.name}단 역할 규칙이 추가되었습니다!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("최애 역할 자동생성", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Section: Official Fanchants & Cheering Guide
        item {
            SectionHeader(
                title = "2. 아이브 공식 응원법 가이드",
                subtitle = "음방 총공 및 콘서트 필수 응원법을 디스코드 채널로 즉시 전송"
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
                    // Song tabs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IVE_SONGS.forEach { song ->
                            val isSelected = song.title == selectedSong.title
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) IvePink else DiscordDarkCard)
                                    .clickable { selectedSong = song }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = song.title.take(6),
                                    color = if (isSelected) Color.White else DiscordTextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "곡명: ${selectedSong.title} (${selectedSong.album})",
                        color = DiscordTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DiscordDarkCard)
                            .padding(12.dp)
                    ) {
                        Column {
                            Text("가사 파트:", color = DiscordTextMuted, fontSize = 11.sp)
                            Text(selectedSong.lyricsSnippet, color = DiscordTextSecondary, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("📢 DIVE 응원법 함성:", color = IvePink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(selectedSong.fanResponse, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.addNotice(
                                title = "📢 [응원법 가이드] 아이브 - ${selectedSong.title}",
                                content = "가사: ${selectedSong.lyricsSnippet}\n\n응원 함성:\n${selectedSong.fanResponse}",
                                channel = "#응원법-가이드",
                                scheduleType = "INSTANT",
                                scheduleDisplay = "즉시 발송",
                                colorHex = "#EC4899"
                            )
                            Toast.makeText(context, "${selectedSong.title} 응원법이 #응원법-가이드 채널에 발송되었습니다!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DiscordBlurple),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("#응원법-가이드 채널로 전송", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section: DIVE Certification Quiz System
        item {
            SectionHeader(
                title = "3. DIVE 공식 팬 인증 & 등업 퀴즈 시스템",
                subtitle = "봇이 디스코드 입장 유저에게 퀴즈를 내어 정답 시 '💎 Official DIVE' 역할을 부여합니다"
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DiscordDarkSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, IvePink.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Quiz, contentDescription = null, tint = IvePink, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "인증 퀴즈 시뮬레이터 (직접 풀어보기)",
                            color = DiscordTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (!quizCompleted) {
                        val currentQ = when (quizStep) {
                            0 -> "Q1. 아이브(IVE)의 공식 팬클럽 명칭은 무엇일까요? (힌트: DIVE)"
                            1 -> "Q2. 아이브의 데뷔곡은 무엇일까요? (힌트: ELEVEN)"
                            else -> "Q3. '완전 럭키비키잖아🍀' 유행어의 주인공 멤버는? (힌트: 장원영)"
                        }

                        Text(
                            text = currentQ,
                            color = DiscordTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = quizAnswerInput,
                                onValueChange = { quizAnswerInput = it },
                                placeholder = { Text("정답 입력...") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("quiz_answer_input"),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    val trimmed = quizAnswerInput.trim().lowercase()
                                    val isCorrect = when (quizStep) {
                                        0 -> trimmed.contains("dive") || trimmed.contains("다이브")
                                        1 -> trimmed.contains("eleven") || trimmed.contains("일레븐")
                                        else -> trimmed.contains("원영") || trimmed.contains("장원영")
                                    }
                                    if (isCorrect) {
                                        quizAnswerInput = ""
                                        if (quizStep < 2) {
                                            quizStep += 1
                                            Toast.makeText(context, "정답입니다! 다음 문제로 넘어갑니다.", Toast.LENGTH_SHORT).show()
                                        } else {
                                            quizCompleted = true
                                            viewModel.addRoleRule(
                                                name = "💎 Official DIVE (인증 팬클럽)",
                                                colorHex = "#EC4899",
                                                emoji = "💎",
                                                ruleType = "VERIFIED_DIVE",
                                                targetChannel = "#팬인증-등업",
                                                desc = "인증 퀴즈 통과로 공식 DIVE 역할 지급 완료"
                                            )
                                        }
                                    } else {
                                        Toast.makeText(context, "아쉬워요! 힌트를 참고하여 다시 입력해보세요.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = IvePink),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("제출", fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        // Success Celebration Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF2E1238))
                                .border(1.dp, IvePink, RoundedCornerShape(8.dp))
                                .padding(14.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                Icon(Icons.Default.Celebration, contentDescription = null, tint = IvePink, modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("축하합니다! 퀴즈 3문항 모두 만점 통과! 🎉", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("디스코드 봇이 '💎 Official DIVE' 역할을 정상 부여했습니다.", color = IvePink, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedButton(
                                    onClick = {
                                        quizCompleted = false
                                        quizStep = 0
                                        quizAnswerInput = ""
                                    }
                                ) {
                                    Text("퀴즈 다시 풀기", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
