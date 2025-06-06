package com.example.jvent.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class EventViewModel : ViewModel() {
    var eventName by mutableStateOf("")
    var ticketCategory by mutableStateOf("")
    var dateTime by mutableStateOf("")
    var location by mutableStateOf("")
    var organizer by mutableStateOf("")
    var platformLink by mutableStateOf("")
    var description by mutableStateOf("")

    fun resetForm() {
        eventName = ""
        ticketCategory = ""
        dateTime = ""
        location = ""
        organizer = ""
        platformLink = ""
        description = ""
    }

    fun validateForm(): Boolean {
        return eventName.isNotBlank() &&
                dateTime.isNotBlank() &&
                location.isNotBlank()
    }
}