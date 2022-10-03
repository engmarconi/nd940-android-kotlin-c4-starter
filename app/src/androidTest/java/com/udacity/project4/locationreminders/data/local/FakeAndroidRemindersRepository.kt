package com.udacity.project4.locationreminders.data.local

import androidx.lifecycle.MutableLiveData
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.runBlocking

class FakeAndroidRemindersRepository : IRemindersRepository {

    private var remindersServiceData: LinkedHashMap<String, ReminderDTO> = LinkedHashMap()
    private val observableReminder = MutableLiveData<Result<List<ReminderDTO>>>()
    private var shouldReturnError = false

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError) {
            return Result.Error("Test error message")
        }
        return Result.Success(remindersServiceData.values.toList())
    }

    suspend fun refreshReminders() {
        observableReminder.value = getReminders()
    }

    override suspend fun saveReminder(reminder: ReminderDTO): Result<Boolean> {
        if (shouldReturnError) {
            return Result.Error("Test error message")
        }
        remindersServiceData[reminder.id] = reminder
        return Result.Success(true)
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

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }
}