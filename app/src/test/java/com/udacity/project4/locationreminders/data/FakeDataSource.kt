package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource() : ReminderDataSource {

    var reminders: MutableList<ReminderDTO> = mutableListOf()
    private var shouldReturnError = false

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        try {
            if (shouldReturnError) {
                return Result.Error("Test error message")
            }
            reminders.let {
                return Result.Success(ArrayList(it))
            }
            return Result.Error("Reminders not found")
        } catch (e: Exception) {
            return Result.Error(e.message)
        }
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        try {
            if (shouldReturnError) {
                return Result.Error("Test error message")
            }
            val reminder = reminders.find { x -> x.id == id }
            reminder?.let {
                return Result.Success(it)
            }
            return Result.Error("Reminder not found")
        } catch (e: Exception) {
            return Result.Error(e.message)
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders.add(reminder)
    }


    override suspend fun deleteAllReminders() {
        reminders.clear()
    }

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }
}