package com.example.jvent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.jvent.repository.EventRepository
import kotlinx.coroutines.launch

class EventListViewModel(private val repository: EventRepository) : ViewModel() {

    // Expose the Flow of events from the repository
    val allEvents = repository.allEvents

    init {
        // Initial refresh when the ViewModel is created
        refreshEvents()
    }

    // Make this function public (remove 'private')
    fun refreshEvents() {
        viewModelScope.launch {
            repository.refreshEvents()
        }
    }
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