package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.*
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest(
    private val remindersDao: RemindersDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ReminderDataSource {

    override suspend fun getReminders(): Result<List<ReminderDTO>> = withContext(ioDispatcher) {
        try {
            val result = remindersDao.getReminders()
            return@withContext Result.Success(result.toList())
        } catch (e: Exception) {
            return@withContext Result.Error(e.message)
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        remindersDao.saveReminder(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> = withContext(ioDispatcher) {
        try {
            val reminder = remindersDao.getReminderById(id)
            if (reminder != null) {
                val result = remindersDao.getReminderById(id)
                return@withContext Result.Success(result!!)
            } else {
                return@withContext Result.Error("Reminder not found!")
            }
        } catch (e: Exception) {
            return@withContext Result.Error(e.message)
        }
    }

    override suspend fun deleteAllReminders() {
        remindersDao.deleteAllReminders()
    }
}