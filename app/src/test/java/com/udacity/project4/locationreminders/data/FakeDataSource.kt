package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource() : ReminderDataSource {

    var reminders: MutableList<ReminderDTO> = mutableListOf()

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        try {
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
}