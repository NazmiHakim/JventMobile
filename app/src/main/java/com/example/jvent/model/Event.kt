package com.example.jvent.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentId // <-- IMPORT THIS
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

// Add the @Entity annotation
@Entity(tableName = "events")
data class Event(
    // Add @PrimaryKey annotation. Since Firestore generates the ID, it can't be null.
    @PrimaryKey
    @DocumentId // <-- ADD THIS ANNOTATION
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val dateTime: String = "",
    val location: String = "",
    val organizer: String = "",
    val platformLink: String = "",
    val ticketCategory: String = "",
    val imageUrl: String = "",
    val userId: String = "",
    val eventType: String = "Gratis",
    val price: String = "",
    @ServerTimestamp val createdAt: Date? = null
)