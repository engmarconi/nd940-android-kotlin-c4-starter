package com.udacity.project4.locationreminders.data

import androidx.lifecycle.MutableLiveData
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.local.IRemindersRepository
import com.udacity.project4.locationreminders.data.local.RemindersDao
import kotlinx.coroutines.runBlocking

class FakeRemindersRepository() : IRemindersRepository {

    //private var remindersServiceData : LinkedHashMap<String, ReminderDTO> = LinkedHashMap()
    private val fakeDataSource = FakeDataSource()
    private val observableReminder = MutableLiveData<Result<List<ReminderDTO>>>()
    private var shouldReturnError = false

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError) {
            return Result.Error("Test error message")
        }
        val result = fakeDataSource.reminders
        return Result.Success(result.toList())
    }

    suspend fun refreshReminders() {
        observableReminder.value = getReminders()
    }

    override suspend fun saveReminder(reminder: ReminderDTO): Result<Boolean> {
        if (shouldReturnError) {
            return Result.Error("Test error message")
        }
        fakeDataSource.saveReminder(reminder)
        return Result.Success(true)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        return fakeDataSource.getReminder(id)
    }

    override suspend fun deleteAllReminders() {
        fakeDataSource.deleteAllReminders()
    }

    suspend fun addReminder(vararg reminders: ReminderDTO) {
        for (reminder in reminders) {
            fakeDataSource.saveReminder(reminder)
        }
        runBlocking { refreshReminders() }
    }

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }
}