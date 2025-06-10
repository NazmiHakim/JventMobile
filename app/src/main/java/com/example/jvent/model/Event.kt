package com.example.jvent.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val dateTime: String = "",
    val location: String = "",
    val organizer: String = "", // <-- Sudah ada
    val platformLink: String = "",
    val ticketCategory: String = "",
    val imageUrl: String = "",
    val userId: String = "",
    val eventType: String = "Gratis", // + Tambahkan field ini
    val price: String = "",           // + Tambahkan field ini
    @ServerTimestamp val createdAt: Date? = null
)