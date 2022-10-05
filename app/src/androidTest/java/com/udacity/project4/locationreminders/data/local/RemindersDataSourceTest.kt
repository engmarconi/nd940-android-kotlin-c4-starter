package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers

import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest

class RemindersDataSourceTest {

    //Add testing implementation to the RemindersDao.kt\

    @get:Rule
    var instanceExecutorRule = InstantTaskExecutorRule()

    private lateinit var localDataSource: ReminderDataSource
    private lateinit var database : RemindersDatabase

    @Before
    fun setup(){
      database =
          Room.inMemoryDatabaseBuilder(
              getApplicationContext(),
              RemindersDatabase::class.java)
              .allowMainThreadQueries()
              .build()

        localDataSource =
            LocalRemindersDataSource(
                database.reminderDao(),
                Dispatchers.Main
            )
    }

    @After
    fun closeDb(){
        database.close()
    }

    @Test
    fun insetReminder_AndGetById() = runBlocking {
        //Given new reminder
        val reminder = ReminderDTO(
            title = "Home", description = "Sweet home", location = "34.1,34.2",
            latitude = 34.1, longitude = 34.2
        )

        //When save new reminder
        localDataSource.saveReminder(reminder)
        val result = localDataSource.getReminder(reminder.id)
        result as Result.Success

        //Then check saved record
        assertThat<ReminderDTO>(result.data as ReminderDTO, notNullValue())
        assertThat(result.data.id, `is`(reminder.id))
        assertThat(result.data.title, `is`(reminder.title))
        assertThat(result.data.location, `is`(reminder.location))
        assertThat(result.data.latitude, `is`(reminder.latitude))
        assertThat(result.data.longitude, `is`(reminder.longitude))
    }

    @Test
    fun updateReminderById_AndGetById() = runBlocking{
        //Given add new reminder
        val reminder = ReminderDTO(
            title = "Home", description = "Sweet home", location = "34.1,34.2",
            latitude = 34.1, longitude = 34.2
        )
        localDataSource.saveReminder(reminder)
        val dbReminderResult = localDataSource.getReminder(reminder.id)
        dbReminderResult as Result.Success
        assertThat<ReminderDTO>(dbReminderResult.data, notNullValue())

        //update reminder
        dbReminderResult.data.title = "Home 2"
        localDataSource.saveReminder(dbReminderResult.data)

        //When get updated record
        val updatedReminder = localDataSource.getReminder(dbReminderResult.data.id)
        updatedReminder as Result.Success

        //Then check updated data
        assertThat<ReminderDTO>(updatedReminder.data, notNullValue())
        assertThat(updatedReminder.data.id, `is`(dbReminderResult.data.id))
        assertThat(updatedReminder.data.title, not(`is`(reminder.title)))
        assertThat(updatedReminder.data.title, `is`("Home 2"))
    }

    @Test
    fun insetReminders_AndClearAll() = runBlocking{
        //Given insert reminders
        val reminder1 = ReminderDTO(
            title = "Home1", description = "Sweet home 2", location = "34.1,34.2",
            latitude = 34.1, longitude = 34.2
        )
        val reminder2 = ReminderDTO(
            title = "Home1", description = "Sweet home 2", location = "34.1,34.2",
            latitude = 34.1, longitude = 34.2
        )

        //When get all, remove all and get all again
        localDataSource.saveReminder(reminder1)
        localDataSource.saveReminder(reminder2)
        val dbReminders = localDataSource.getReminders()
        dbReminders as Result.Success

        localDataSource.deleteAllReminders()
        val afterDeleteReminders = localDataSource.getReminders()
        afterDeleteReminders as Result.Success

        //Then check no record
        assertThat(dbReminders.data.size, `is`(2))
        assertThat(afterDeleteReminders.data.size, `is`(0))
    }

    @Test
    fun getReminder_NotFound() = runBlocking{
        //Given remove all
        localDataSource.deleteAllReminders()

        //When save new reminder
        val notFoundReminder = localDataSource.getReminder("15220")
        notFoundReminder as Result.Error

        //Then check not found record is null
        Assert.assertThat(notFoundReminder.message, `is`("Reminder not found!"))
    }
}