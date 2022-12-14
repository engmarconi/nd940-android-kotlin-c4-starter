package com.udacity.project4.locationreminders.reminderslist

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeRemindersRepository
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.IRemindersRepository
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.*
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext.get
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    private lateinit var viewModel: RemindersListViewModel
    private lateinit var repository: FakeRemindersRepository

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()


    @Before
    fun setupViewModel() = runBlockingTest {
        repository = FakeRemindersRepository()
        val reminder1 = ReminderDTO("Title1", "Description1", "", 0.0, 0.0)
        val reminder2 = ReminderDTO("Title1", "Description1", "", 0.0, 0.0)
        repository.addReminder(reminder1, reminder2)
        viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), repository)
    }

    @Test
    fun saveReminder_normal_case() {
        // Then the new task event is triggered
        viewModel.loadReminders()
        val size = viewModel.remindersList.value?.size

        Assert.assertEquals(size, 2)
    }

    @Test
    fun saveReminder_checkLoading_case() {
        // Then the new task event is triggered
        viewModel.loadReminders()
        val size = viewModel.remindersList.value?.size

        Assert.assertEquals(size, 2)
    }

    @Test
    fun loadRemindersWhenAreUnavailable_callErrorToDisplay() {
        // Make the repository return errors
        repository.setReturnError(true)
        viewModel.loadReminders()

        // Then an error message is shown
        MatcherAssert.assertThat(
            viewModel.showSnackBar.getOrAwaitValue(),
            CoreMatchers.`is`("Test error message")
        )
    }

    @Test
    fun loadReminderByIdWhenAreUnavailable_callErrorToDisplay() {
        // Make the repository return errors
        repository.setReturnError(true)
        viewModel.getReminderById("2121")

        // Then an error message is shown
        MatcherAssert.assertThat(
            viewModel.showSnackBar.getOrAwaitValue(),
            CoreMatchers.`is`("Test error message")
        )
    }

    @Test
    fun checkLoading_loadReminder() {
        mainCoroutineRule.pauseDispatcher()

        // Load the task in the viewmodel
        viewModel.loadReminders()

        // Then progress indicator is shown
        MatcherAssert.assertThat(
            viewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(true)
        )

        // Execute pending coroutines actions
        mainCoroutineRule.resumeDispatcher()

        // Then progress indicator is hidden
        MatcherAssert.assertThat(
            viewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(false)
        )
    }
}