package com.example.jvent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jvent.repository.EventRepository

class EventListViewModel(repository: EventRepository) : ViewModel() {

    // Expose the Flow of events from the repository
    val allEvents = repository.allEvents

    // The init block and refreshEvents function are no longer needed
    // as the repository now handles real-time updates automatically.
}

// Factory to create EventListViewModel with the repository (remains the same)
class EventViewModelFactory(private val repository: EventRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EventListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}