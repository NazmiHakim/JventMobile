package com.example.jvent.viewmodel

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jvent.ImgurApiClient
import com.example.jvent.MainActivity
import com.example.jvent.R
import com.example.jvent.model.Event
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class EventViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    private val imgurApiService = ImgurApiClient.apiService

    var eventName by mutableStateOf("")
    var ticketCategory by mutableStateOf("")
    var dateTime by mutableStateOf("")
    var location by mutableStateOf("")
    var organizer by mutableStateOf("")
    var platformLink by mutableStateOf("")
    var description by mutableStateOf("")
    var imageUrl by mutableStateOf<String?>(null) // Holds the existing image URL when editing
    var imageUri by mutableStateOf<Uri?>(null) // Holds the new local image URI
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    // + Tambahkan state untuk field baru
    var eventType by mutableStateOf("Gratis") // Opsi: "Gratis" atau "Berbayar"
    var price by mutableStateOf("")

    private var eventUserId: String? = null // To store the creator's ID

    fun resetForm() {
        eventName = ""
        ticketCategory = ""
        dateTime = ""
        location = ""
        organizer = ""
        platformLink = ""
        description = ""
        imageUri = null
        imageUrl = null
        eventUserId = null
        // + Reset state baru
        eventType = "Gratis"
        price = ""
    }

    // * Perbarui validasi
    private fun validateForm(isUpdate: Boolean = false): Boolean {
        error = null // Reset error sebelum validasi
        return when {
            !isUpdate && imageUri == null -> {
                error = "Poster/gambar event tidak boleh kosong"
                false
            }
            isUpdate && imageUrl.isNullOrBlank() && imageUri == null -> {
                error = "Poster/gambar event tidak boleh kosong"
                false
            }
            eventName.isBlank() -> {
                error = "Nama event tidak boleh kosong"
                false
            }
            dateTime.isBlank() -> {
                error = "Tanggal dan waktu tidak boleh kosong"
                false
            }
            location.isBlank() -> {
                error = "Lokasi tidak boleh kosong"
                false
            }
            organizer.isBlank() -> {
                error = "Penyelenggara event tidak boleh kosong"
                false
            }
            eventType == "Berbayar" && price.isBlank() -> {
                error = "Harga event tidak boleh kosong jika event berbayar"
                false
            }
            else -> true
        }
    }

    fun loadEvent(eventId: String) {
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                val doc = firestore.collection("events").document(eventId).get().await()
                doc.toObject(Event::class.java)?.let { event ->
                    eventName = event.title
                    description = event.description
                    dateTime = event.dateTime
                    location = event.location
                    organizer = event.organizer
                    platformLink = event.platformLink
                    ticketCategory = event.ticketCategory
                    imageUrl = event.imageUrl // Store the original image URL
                    eventType = event.eventType
                    price = event.price
                    eventUserId = event.userId
                } ?: run {
                    error = "Event not found"
                }
            } catch (e: Exception) {
                error = "Failed to load event: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun createEvent(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (!validateForm(isUpdate = false)) {
            onError(error!!)
            return
        }

        val currentUser = auth.currentUser
        if (currentUser == null) {
            onError("User must be logged in")
            return
        }

        viewModelScope.launch {
            isLoading = true
            error = null

            try {
                val imageUrl = uploadImageToImgur(
                    imageUri = imageUri!!,
                    title = eventName,
                    description = description,
                    clientId = "cff49ba6c9e160f",
                    context = context
                )

                val event = Event(
                    title = eventName,
                    description = description,
                    dateTime = dateTime,
                    location = location,
                    organizer = organizer,
                    platformLink = platformLink,
                    ticketCategory = ticketCategory,
                    imageUrl = imageUrl,
                    userId = currentUser.uid,
                    eventType = eventType,
                    price = if (eventType == "Gratis") "Gratis" else price
                )

                saveEventToFirestore(event)
                sendNewEventNotification(context) // <-- PANGGIL FUNGSI NOTIFIKASI DI SINI
                onSuccess()
            } catch (e: Exception) {
                error = e.message ?: "Gagal membuat event"
                onError(error!!)
            } finally {
                isLoading = false
            }
        }
    }

    private fun sendNewEventNotification(context: Context) {
        // Create an explicit intent for an Activity in your app
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, "NEW_EVENT_CHANNEL_ID")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Ganti dengan ikon notifikasi yang sesuai
            .setContentTitle("Ada event baru nih!")
            .setContentText("cek sekarang yuk!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // notificationId is a unique int for each notification that you must define
                notify(System.currentTimeMillis().toInt(), builder.build())
            }
        }
    }

    fun updateEvent(
        context: Context,
        eventId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (!validateForm(isUpdate = true)) {
            onError(error!!)
            return
        }

        viewModelScope.launch {
            isLoading = true
            error = null

            try {
                val finalImageUrl = if (imageUri != null) {
                    uploadImageToImgur(
                        imageUri = imageUri!!,
                        title = eventName,
                        description = description,
                        clientId = "cff49ba6c9e160f",
                        context = context
                    )
                } else {
                    imageUrl ?: throw IllegalStateException("Image URL is null")
                }

                val eventData = mapOf(
                    "title" to eventName,
                    "description" to description,
                    "dateTime" to dateTime,
                    "location" to location,
                    "organizer" to organizer,
                    "platformLink" to platformLink,
                    "ticketCategory" to ticketCategory,
                    "imageUrl" to finalImageUrl,
                    "eventType" to eventType,
                    "price" to if (eventType == "Gratis") "Gratis" else price
                )

                firestore.collection("events").document(eventId).update(eventData).await()
                onSuccess()
            } catch (e: Exception) {
                error = e.message ?: "Failed to update event"
                onError(error!!)
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteEvent(
        eventId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                firestore.collection("events").document(eventId).delete().await()
                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                error = e.message ?: "Failed to delete event"
                withContext(Dispatchers.Main) {
                    onError(error!!)
                }
            } finally {
                isLoading = false
            }
        }
    }

    private suspend fun uploadImageToImgur(
        imageUri: Uri,
        title: String,
        description: String,
        clientId: String,
        context: Context
    ): String {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val file = withContext(Dispatchers.IO) {
            File.createTempFile("img", ".jpg").apply {
                inputStream?.use { input ->
                    outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                inputStream?.close()
            }
        }
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
        val titlePart = title.toRequestBody("text/plain".toMediaTypeOrNull())
        val descriptionPart = description.toRequestBody("text/plain".toMediaTypeOrNull())
        val response = imgurApiService.uploadImage(
            authHeader = "Client-ID $clientId",
            image = imagePart,
            title = titlePart,
            description = descriptionPart
        )
        if (response.isSuccessful && response.body()?.success == true) {
            return response.body()?.data?.link ?: throw Exception("Image link not found")
        } else {
            throw Exception(response.message() ?: "Failed to upload image to Imgur")
        }
    }

    private suspend fun saveEventToFirestore(event: Event) {
        firestore.collection("events").add(event).await()
    }
}