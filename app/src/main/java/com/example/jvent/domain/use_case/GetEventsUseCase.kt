package com.example.jvent.domain.use_case

import com.example.jvent.domain.repository.EventRepository
import com.example.jvent.model.Event
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEventsUseCase @Inject constructor(
    private val repository: EventRepository
) {
    operator fun invoke(): Flow<List<Event>> = repository.getEvents()
}