package com.example.jvent.domain.use_case

import com.example.jvent.domain.repository.EventRepository
import com.example.jvent.model.Event
import javax.inject.Inject

class GetEventByIdUseCase @Inject constructor(
    private val repository: EventRepository
) {
    suspend operator fun invoke(eventId: String): Event? = repository.getEventById(eventId)
}