package com.example.jvent.domain.use_case

import com.example.jvent.domain.repository.EventRepository
import javax.inject.Inject

class DeleteEventUseCase @Inject constructor(
    private val repository: EventRepository
) {
    suspend operator fun invoke(eventId: String) = repository.deleteEvent(eventId)
}