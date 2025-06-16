package com.example.jvent.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val dateTime: String,
    val location: String,
    val organizer: String,
    val platformLink: String,
    val ticketCategory: String,
    val imageUrl: String,
    val eventType: String,
    val price: String,
    val userId: String,
    val fetchedAt: Long = System.currentTimeMillis() // To manage cache staleness
)

// Helper function to map from the network model to the database entity
fun Event.toEntity(): EventEntity {
    return EventEntity(
        id = this.id,
        title = this.title,
        description = this.description,
        dateTime = this.dateTime,
        location = this.location,
        organizer = this.organizer,
        platformLink = this.platformLink,
        ticketCategory = this.ticketCategory,
        imageUrl = this.imageUrl,
        eventType = this.eventType,
        price = this.price,
        userId = this.userId
    )
}

// Helper function to map from the database entity to the network model
fun EventEntity.toModel(): Event {
    return Event(
        id = this.id,
        title = this.title,
        description = this.description,
        dateTime = this.dateTime,
        location = this.location,
        organizer = this.organizer,
        platformLink = this.platformLink,
        ticketCategory = this.ticketCategory,
        imageUrl = this.imageUrl,
        eventType = this.eventType,
        price = this.price,
        userId = this.userId
    )
}