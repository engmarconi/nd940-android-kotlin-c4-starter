package com.udacity.project4.locationreminders.savereminder

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeRemindersRepository
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    private lateinit var viewModel: SaveReminderViewModel
    private lateinit var repository: FakeRemindersRepository

    @Before
    fun setupViewModel() = runBlockingTest {
        repository = FakeRemindersRepository()
        viewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), repository)
    }

    @Test
    fun saveReminder_normal_case() {
        // Given a fresh ViewModel
        val reminder = ReminderDataItem("Home","Sweet Home","32.0,33.0",32.0,33.0)
        // When adding a new task
        viewModel.saveReminder(reminder)

        // Then the new task event is triggered
        val value = viewModel.showToast.value
       Assert.assertEquals(value, "Reminder Saved !")
    }
}