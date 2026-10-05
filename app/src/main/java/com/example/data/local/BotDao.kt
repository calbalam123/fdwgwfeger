package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BotDao {
    // Config
    @Query("SELECT * FROM bot_config WHERE id = 1 LIMIT 1")
    fun getBotConfig(): Flow<BotConfigEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateConfig(config: BotConfigEntity)

    // Tickets
    @Query("SELECT * FROM tickets ORDER BY id DESC")
    fun getAllTickets(): Flow<List<TicketEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: TicketEntity)

    @Update
    suspend fun updateTicket(ticket: TicketEntity)

    @Query("DELETE FROM tickets WHERE id = :id")
    suspend fun deleteTicketById(id: Long)

    // Auto Notices
    @Query("SELECT * FROM auto_notices ORDER BY id DESC")
    fun getAllNotices(): Flow<List<AutoNoticeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotice(notice: AutoNoticeEntity)

    @Update
    suspend fun updateNotice(notice: AutoNoticeEntity)

    @Query("DELETE FROM auto_notices WHERE id = :id")
    suspend fun deleteNoticeById(id: Long)

    // Role Rules
    @Query("SELECT * FROM role_rules ORDER BY id ASC")
    fun getAllRoleRules(): Flow<List<RoleRuleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoleRule(rule: RoleRuleEntity)

    @Update
    suspend fun updateRoleRule(rule: RoleRuleEntity)

    @Query("DELETE FROM role_rules WHERE id = :id")
    suspend fun deleteRoleRuleById(id: Long)

    // Logs
    @Query("SELECT * FROM bot_logs ORDER BY id DESC LIMIT :limit")
    fun getRecentLogs(limit: Int = 100): Flow<List<BotLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: BotLogEntity)

    @Query("DELETE FROM bot_logs")
    suspend fun clearLogs()
}
