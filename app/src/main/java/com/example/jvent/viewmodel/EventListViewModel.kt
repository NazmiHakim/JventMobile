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
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class EventListViewModel(repository: EventRepository) : ViewModel() {

    private val allEvents: StateFlow<List<Event>> = repository.allEvents
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

    private val _selectedFilter = MutableStateFlow("current") // "current" or "past"
    val selectedFilter: StateFlow<String> = _selectedFilter

    // State untuk filter waktu dan harga
    private val _timeFilter = MutableStateFlow<String?>(null)
    val timeFilter: StateFlow<String?> = _timeFilter

    private val _priceFilter = MutableStateFlow<String?>(null)
    val priceFilter: StateFlow<String?> = _priceFilter

    val filteredEvents: StateFlow<List<Event>> =
        combine(allEvents, _searchQuery, _selectedFilter, _timeFilter, _priceFilter) { events, query, filter, timeFilter, priceFilter ->
            val now = Date()
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

            val filteredByDate = events.filter { event ->
                val eventDate = try {
                    dateFormat.parse(event.dateTime)
                } catch (e: Exception) {
                    null
                }

                if (eventDate == null) {
                    false
                } else {
                    val pastDateThreshold = Date(eventDate.time + TimeUnit.DAYS.toMillis(1))

                    if (filter == "current") {
                        pastDateThreshold.after(now)
                    } else {
                        pastDateThreshold.before(now)
                    }
                }
            }

            val filteredByTime = timeFilter?.let { filterValue ->
                val calendar = Calendar.getInstance()
                calendar.firstDayOfWeek = Calendar.MONDAY

                fun Calendar.toStartOfDay(): Calendar {
                    this.set(Calendar.HOUR_OF_DAY, 0)
                    this.set(Calendar.MINUTE, 0)
                    this.set(Calendar.SECOND, 0)
                    this.set(Calendar.MILLISECOND, 0)
                    return this
                }

                fun Calendar.toEndOfDay(): Calendar {
                    this.set(Calendar.HOUR_OF_DAY, 23)
                    this.set(Calendar.MINUTE, 59)
                    this.set(Calendar.SECOND, 59)
                    this.set(Calendar.MILLISECOND, 999)
                    return this
                }

                when (filterValue) {
                    "Hari Ini" -> {
                        val todayStart = Calendar.getInstance().toStartOfDay().time
                        val todayEnd = Calendar.getInstance().toEndOfDay().time
                        filteredByDate.filter {
                            val eventDate = dateFormat.parse(it.dateTime)
                            eventDate != null && eventDate in todayStart..todayEnd
                        }
                    }
                    "Besok" -> {
                        val tomorrowStart = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }.toStartOfDay().time
                        val tomorrowEnd = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }.toEndOfDay().time
                        filteredByDate.filter {
                            val eventDate = dateFormat.parse(it.dateTime)
                            eventDate != null && eventDate in tomorrowStart..tomorrowEnd
                        }
                    }
                    "Minggu Ini" -> {
                        val startOfWeek = calendar.apply { set(Calendar.DAY_OF_WEEK, Calendar.MONDAY) }.toStartOfDay().time
                        val endOfWeek = calendar.apply { add(Calendar.DAY_OF_WEEK, 6) }.toEndOfDay().time
                        filteredByDate.filter {
                            val eventDate = dateFormat.parse(it.dateTime)
                            eventDate != null && eventDate in startOfWeek..endOfWeek
                        }
                    }
                    "Minggu Depan" -> {
                        val startOfNextWeek = calendar.apply { add(Calendar.WEEK_OF_YEAR, 1); set(Calendar.DAY_OF_WEEK, Calendar.MONDAY) }.toStartOfDay().time
                        val endOfNextWeek = calendar.apply { add(Calendar.DAY_OF_WEEK, 6) }.toEndOfDay().time
                        filteredByDate.filter {
                            val eventDate = dateFormat.parse(it.dateTime)
                            eventDate != null && eventDate in startOfNextWeek..endOfNextWeek
                        }
                    }
                    "Bulan Depan" -> {
                        val startOfNextMonth = calendar.apply { add(Calendar.MONTH, 1); set(Calendar.DAY_OF_MONTH, 1) }.toStartOfDay().time
                        val endOfNextMonth = calendar.apply { set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH)) }.toEndOfDay().time
                        filteredByDate.filter {
                            val eventDate = dateFormat.parse(it.dateTime)
                            eventDate != null && eventDate in startOfNextMonth..endOfNextMonth
                        }
                    }
                    else -> filteredByDate
                }
            } ?: filteredByDate

            val filteredByPrice = priceFilter?.let { filterValue ->
                filteredByTime.filter { event ->
                    when (filterValue) {
                        "Gratis" -> event.eventType == "Gratis" || event.eventType == "Free"
                        "Berbayar" -> event.eventType == "Berbayar" || event.eventType == "Paid"
                        else -> true
                    }
                }
            } ?: filteredByTime

            if (query.isBlank()) {
                filteredByPrice
            } else {
                filteredByPrice.filter { it.title.contains(query, ignoreCase = true) }
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

    fun onTimeFilterChange(filter: String?) {
        _timeFilter.value = if (_timeFilter.value == filter) null else filter
    }

    fun onPriceFilterChange(filter: String?) {
        _priceFilter.value = if (_priceFilter.value == filter) null else filter
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