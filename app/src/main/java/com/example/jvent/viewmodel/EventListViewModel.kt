package com.example.jvent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.jvent.model.Event
import com.example.jvent.repository.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class EventListViewModel(repository: EventRepository) : ViewModel() {

    val allEvents: StateFlow<List<Event>> = repository.allEvents
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    val favoriteEvents: StateFlow<List<Event>> = repository.favoriteEvents
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedFilter = MutableStateFlow("current") // "current" atau "past"
    val selectedFilter: StateFlow<String> = _selectedFilter

    val filteredEvents: StateFlow<List<Event>> =
        combine(allEvents, _searchQuery, _selectedFilter) { events, query, filter ->
            val now = Date()
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

            val filtered = events.filter { event ->
                val eventDate = try {
                    dateFormat.parse(event.dateTime)
                } catch (e: Exception) {
                    null
                }

                if (eventDate == null) {
                    false // Abaikan event dengan format tanggal yang salah
                } else {
                    // Batas waktu adalah 1 hari setelah event berakhir
                    val pastDateThreshold = Date(eventDate.time + TimeUnit.DAYS.toMillis(1))

                    if (filter == "current") {
                        pastDateThreshold.after(now) // Event dianggap "sekarang" jika belum melewati 1 hari
                    } else {
                        pastDateThreshold.before(now) // Event dianggap "lalu" jika sudah melewati 1 hari
                    }
                }
            }

            if (query.isBlank()) {
                filtered
            } else {
                filtered.filter { it.title.contains(query, ignoreCase = true) }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onFilterChange(filter: String) {
        _selectedFilter.value = filter
    }
}

class EventViewModelFactory(private val repository: EventRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EventListViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EventViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}