package com.udacity.project4.locationreminders.data.local

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalRemindersDataSource internal constructor(
    private val remindersDao: RemindersDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ReminderDataSource {

    override suspend fun getReminders(): Result<List<ReminderDTO>> = withContext(ioDispatcher) {
        return@withContext try {
            return@withContext Result.Success(remindersDao.getReminders())
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    }
    override suspend fun saveReminder(reminder: ReminderDTO) {
        remindersDao.saveReminder(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> = withContext(ioDispatcher){
        return@withContext try {
            val reminder = remindersDao.getReminderById(id)
            if (reminder != null) {
                return@withContext Result.Success(reminder)
            } else {
                return@withContext Result.Error("Reminder not found!")
            }
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    }

    override suspend fun deleteAllReminders() {
      remindersDao.deleteAllReminders()
    }
}