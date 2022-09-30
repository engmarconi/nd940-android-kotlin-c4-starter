package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.FakeAndroidRemindersRepository
import com.udacity.project4.locationreminders.data.local.IRemindersRepository
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.atPositionOnView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest : AutoCloseKoinTest() {

    private lateinit var repository: IRemindersRepository
    private lateinit var appContext: Application

    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as IRemindersRepository
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as IRemindersRepository
                )
            }
            single { FakeAndroidRemindersRepository() as IRemindersRepository }
            single { LocalDB.createRemindersDao(appContext) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = get()

        //clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }
    }

    //test the displayed data on the UI.
    @Test
    fun remindersList_DisplayedInUi() = runBlockingTest {
        // GIVEN - Add add reminders
        val reminder1 = ReminderDTO("Title1", "Description1", "", 0.0, 0.0)
        val reminder2 = ReminderDTO("Title1", "Description1", "", 0.0, 0.0)
        repository.saveReminder(reminder1)
        repository.saveReminder(reminder2)

        // WHEN - Open reminders list
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        //THEN - Check no data text view not visible
        onView(withId(R.id.noDataTextView)).check(matches(not(isDisplayed())))
        onView(withId(R.id.reminderssRecyclerView))
            .check(matches(atPositionOnView(0, ViewMatchers.withText(reminder1.title), R.id.title)));
        Thread.sleep(2000)
    }


    //test the navigation of the fragments.
    @Test
    fun clickReminderList_navigateToNewReminder() = runBlockingTest {
        // GIVEN - Add add reminders
        val reminder1 = ReminderDTO("Title1", "Description1", "", 0.0, 0.0)
        val reminder2 = ReminderDTO("Title1", "Description1", "", 0.0, 0.0)
        repository.saveReminder(reminder1)
        repository.saveReminder(reminder2)

        // WHEN - Open reminders list
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
        onView(withId(R.id.addReminderFAB)).perform(click())

        //THEN - Check no data text view not visible
        verify(navController).navigate(ReminderListFragmentDirections.toSaveReminder())
    }

    //add testing for the error messages.
    @Test
    fun remindersList_TestErrorMessage() = runBlockingTest {
        // GIVEN - Add reminder & set test error message flag
        (repository as FakeAndroidRemindersRepository).setReturnError(true)
        val reminder = ReminderDTO("Title1", "Description1", "", 0.0, 0.0)
        repository.saveReminder(reminder)

        // WHEN - Open reminders list
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        //THEN - Check snackbar message as "Test error message"
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText("Test error message")))
        Thread.sleep(1000)
    }
}