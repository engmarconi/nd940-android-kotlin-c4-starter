package com.udacity.project4.locationreminders.data

import androidx.lifecycle.MutableLiveData
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.local.IRemindersRepository
import com.udacity.project4.locationreminders.data.local.RemindersDao
import kotlinx.coroutines.runBlocking

class FakeRemindersRepository : IRemindersRepository {

    private var remindersServiceData : LinkedHashMap<String, ReminderDTO> = LinkedHashMap()
    private val observableReminder = MutableLiveData<Result<List<ReminderDTO>>>()

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return Result.Success(remindersServiceData.values.toList())
    }

    suspend fun refreshReminders() {
        observableReminder.value = getReminders()
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        remindersServiceData[reminder.id] = reminder
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        val reminder = remindersServiceData[id]
        reminder?.let {
            return Result.Success(reminder)
        }
        return Result.Error("Reminder not found")
    }

    override suspend fun deleteAllReminders() {
        remindersServiceData.clear()
    }

    fun addReminder(vararg reminders: ReminderDTO) {
        for (reminder in reminders) {
            remindersServiceData[reminder.id] = reminder
        }
        runBlocking { refreshReminders() }
    }

}