package net.yuuzu.data.model.schedule

import com.mongodb.client.model.Filters.eq
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.expr
import org.litote.kmongo.match

class ScheduleDataSourceImpl(
    db: CoroutineDatabase
): ScheduleDataSource {
    private val scheduleDb = db.getCollection<Schedule>()

    override suspend fun getSchedule(id: String): Schedule? {
        return scheduleDb.findOne(Schedule::id eq id)
    }

    override suspend fun getScheduleByUser(userId: String): List<Schedule> {
        return scheduleDb.find(Schedule::userId eq userId).toList()
    }

    override suspend fun getSchedules(): List<Schedule> {
        return scheduleDb.find().toList()
    }

    override suspend fun insertSchedule(schedule: Schedule): Boolean {
        return scheduleDb.insertOne(schedule).wasAcknowledged()
    }

    override suspend fun updateSchedule(schedule: Schedule): Boolean {
        return scheduleDb.updateOneById(schedule.id, schedule).wasAcknowledged()
    }
}