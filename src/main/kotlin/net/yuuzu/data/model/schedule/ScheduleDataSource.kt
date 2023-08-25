package net.yuuzu.data.model.schedule

interface ScheduleDataSource {
    suspend fun getSchedule(id: String): Schedule?
    suspend fun getScheduleByUser(userId: String): List<Schedule>
    suspend fun getSchedules(): List<Schedule>
    suspend fun insertSchedule(schedule: Schedule): Boolean
    suspend fun updateSchedule(schedule: Schedule): Boolean
}