package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    //Add testing implementation to the RemindersDao.kt\

    @get:Rule
    var instanceExecutorRule = InstantTaskExecutorRule()

    private lateinit var database : RemindersDatabase

    @Before
    fun initDb(){
      database = Room.inMemoryDatabaseBuilder(getApplicationContext(), RemindersDatabase::class.java)
          .build()
    }

    @After
    fun closeDb(){
        database.close()
    }

    @Test
    fun insetReminder_AndGetById() = runBlockingTest{
        //Given new reminder
        val reminder = ReminderDTO(
            title = "Home", description = "Sweet home", location = "34.1,34.2",
            latitude = 34.1, longitude = 34.2
        )

        //When save new reminder
        database.reminderDao().saveReminder(reminder)
        val dbReminder = database.reminderDao().getReminderById(reminder.id)

        //Then check saved record
        assertThat<ReminderDTO>(dbReminder as ReminderDTO, notNullValue())
        assertThat(dbReminder.id, `is`(reminder.id))
        assertThat(dbReminder.title, `is`(reminder.title))
        assertThat(dbReminder.location, `is`(reminder.location))
        assertThat(dbReminder.latitude, `is`(reminder.latitude))
        assertThat(dbReminder.longitude, `is`(reminder.longitude))
    }

    @Test
    fun updateReminderById_AndGetById() = runBlockingTest{
        //Given add new reminder
        val reminder = ReminderDTO(
            title = "Home", description = "Sweet home", location = "34.1,34.2",
            latitude = 34.1, longitude = 34.2
        )
        database.reminderDao().saveReminder(reminder)
        val dbReminder = database.reminderDao().getReminderById(reminder.id)
        //update reminder
        dbReminder?.title = "Home 2"
        database.reminderDao().saveReminder(dbReminder!!)

        //When get updated record
        val updatedReminder = database.reminderDao().getReminderById(dbReminder.id)

        //Then check updated data
        assertThat<ReminderDTO>(updatedReminder as ReminderDTO, notNullValue())
        assertThat(updatedReminder.id, `is`(dbReminder.id))
        assertThat(updatedReminder.title, not(`is`(reminder.title)))
        assertThat(updatedReminder.title, `is`("Home 2"))
    }

    @Test
    fun insetReminders_AndClearAll() = runBlockingTest{
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
        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)
        val dbReminders = database.reminderDao().getReminders()
        database.reminderDao().deleteAllReminders()
        val afterDeleteReminders = database.reminderDao().getReminders()

        //Then check no record
        assertThat(dbReminders.size, `is`(2))
        assertThat(afterDeleteReminders.size, `is`(0))
    }

    @Test
    fun getReminder_NotFound() = runBlockingTest{
        //Given remove all
        database.reminderDao().deleteAllReminders()

        //When save new reminder
        val notFoundReminder = database.reminderDao().getReminderById("15220")

        //Then check not found record is null
        assertThat(notFoundReminder, nullValue())
    }
}