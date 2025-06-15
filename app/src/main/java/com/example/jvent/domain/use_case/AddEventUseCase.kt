package com.example.jvent.domain.use_case

import com.example.jvent.domain.repository.EventRepository
import com.example.jvent.model.Event
import javax.inject.Inject

class AddEventUseCase @Inject constructor(
    private val repository: EventRepository
) {
    suspend operator fun invoke(event: Event) = repository.addEvent(event)
}