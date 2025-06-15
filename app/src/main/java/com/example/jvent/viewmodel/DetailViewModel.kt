package com.example.jvent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jvent.domain.use_case.DeleteEventUseCase
import com.example.jvent.domain.use_case.GetEventsUseCase
import com.example.jvent.model.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getEventUseCase: GetEventsUseCase,
    private val deleteEventUseCase: DeleteEventUseCase
) : ViewModel() {

    private val _event = MutableStateFlow<Event?>(null)
    val event = _event.asStateFlow()

    fun loadEvent(eventId: String) {
        viewModelScope.launch {
            _event.value = getEventUseCase(eventId)
        }
    }

    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            deleteEventUseCase(eventId)
        }
    }
}