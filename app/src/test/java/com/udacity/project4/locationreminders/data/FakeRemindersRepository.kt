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
        return try {
            fakeDataSource.setReturnError(shouldReturnError)
            fakeDataSource.getReminders()
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    }

    suspend fun refreshReminders() {
        observableReminder.value = getReminders()
    }

    override suspend fun saveReminder(reminder: ReminderDTO): Result<Boolean> {
        return try {
            fakeDataSource.setReturnError(shouldReturnError)
            fakeDataSource.saveReminder(reminder)
            return Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        return try {
            fakeDataSource.setReturnError(shouldReturnError)
            fakeDataSource.getReminder(id)
        } catch (e: Exception) {
            Result.Error(e.message)
        }
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