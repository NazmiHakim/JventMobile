package com.example.jvent.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@Entity(tableName = "events")
data class Event(
    @PrimaryKey
    @DocumentId
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val dateTime: String = "",
    val location: String = "",
    val organizer: String = "",
    val platformLink: String = "",
    val imageUrl: String = "",
    val userId: String = "",
    val eventType: String = "Gratis",
    val price: String = "",
    @ServerTimestamp val createdAt: Date? = null,
    var isFavorite: Boolean = false
)