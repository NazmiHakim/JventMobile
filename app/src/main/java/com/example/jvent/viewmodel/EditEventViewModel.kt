// app/src/main/java/com/example/jvent/viewmodel/EditEventViewModel.kt
package com.example.jvent.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jvent.domain.use_case.GetEventByIdUseCase
import com.example.jvent.domain.use_case.UpdateEventUseCase
import com.example.jvent.model.Event
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditEventViewModel @Inject constructor(
    private val getEventByIdUseCase: GetEventByIdUseCase,
    private val updateEventUseCase: UpdateEventUseCase
) : ViewModel() {

    var eventName by mutableStateOf("")
    var ticketCategory by mutableStateOf("")
    var dateTime by mutableStateOf("")
    var location by mutableStateOf("")
    var organizer by mutableStateOf("")
    var platformLink by mutableStateOf("")
    var description by mutableStateOf("")
    var imageUrl by mutableStateOf<String?>(null)
    var imageUri by mutableStateOf<Uri?>(null) // For new image selection
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var eventType by mutableStateOf("Gratis")
    var price by mutableStateOf("")
    private var currentEventId by mutableStateOf("")
    private var originalUserId by mutableStateOf("")


    fun loadEvent(eventId: String) {
        if (eventId == currentEventId) return
        viewModelScope.launch {
            isLoading = true
            getEventByIdUseCase(eventId)?.let { event ->
                currentEventId = event.id
                eventName = event.title
                ticketCategory = event.ticketCategory
                dateTime = event.dateTime
                location = event.location
                organizer = event.organizer
                platformLink = event.platformLink
                description = event.description
                imageUrl = event.imageUrl
                eventType = event.eventType
                price = event.price
                originalUserId = event.userId
            }
            isLoading = false
        }
    }

    private fun validateForm(): Boolean {
        return when {
            eventName.isBlank() -> {
                error = "Event name cannot be empty"
                false
            }
            dateTime.isBlank() -> {
                error = "Date and time cannot be empty"
                false
            }
            location.isBlank() -> {
                error = "Location cannot be empty"
                false
            }
            organizer.isBlank() -> {
                error = "Event organizer cannot be empty"
                false
            }
            eventType == "Berbayar" && price.isBlank() -> {
                error = "Price cannot be empty for a paid event"
                false
            }
            else -> true
        }
    }

    fun updateEvent(context: Context, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (!validateForm()) {
            onError(error ?: "Invalid form")
            return
        }

        viewModelScope.launch {
            isLoading = true
            error = null

            try {
                // For this example, we assume image editing is not implemented
                // to avoid the complexity of re-uploading to Imgur.
                // A real implementation would handle new image uploads.
                if (imageUri != null) {
                    // Handle new image upload here if necessary
                }

                val updatedEvent = Event(
                    id = currentEventId,
                    title = eventName,
                    description = description,
                    dateTime = dateTime,
                    location = location,
                    organizer = organizer,
                    platformLink = platformLink,
                    ticketCategory = ticketCategory,
                    imageUrl = imageUrl ?: "",
                    eventType = eventType,
                    price = if (eventType == "Gratis") "Gratis" else price,
                    userId = originalUserId
                )

                updateEventUseCase(updatedEvent)
                onSuccess()

            } catch (e: Exception) {
                error = e.message ?: "Failed to update event"
                onError(error!!)
            } finally {
                isLoading = false
            }
        }
    }
}