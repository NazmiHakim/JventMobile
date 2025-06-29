package com.example.jvent.viewmodel

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jvent.ImgurApiClient
import com.example.jvent.R
import com.example.jvent.model.Event
import com.example.jvent.repository.EventRepository
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class EventViewModel(private val repository: EventRepository? = null) : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    private val imgurApiService = ImgurApiClient.apiService
    private var analytics: FirebaseAnalytics = Firebase.analytics

    var eventName by mutableStateOf("")
    var dateTime by mutableStateOf("")
    var location by mutableStateOf("")
    var organizer by mutableStateOf("")
    var platformLink by mutableStateOf("")
    var description by mutableStateOf("")
    var imageUrl by mutableStateOf<String?>(null)
    var imageUri by mutableStateOf<Uri?>(null)
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var eventType by mutableStateOf("")
    var price by mutableStateOf("")
    private var eventUserId: String? = null

    // --- FUNGSI BARU UNTUK UI ---
    fun getEventById(eventId: String): Flow<Event?> {
        return repository!!.getEventById(eventId)
    }
    // ---------------------------

    // --- FUNGSI UPDATE FAVORIT (HANYA LOKAL) ---
    fun updateFavoriteStatus(event: Event, isFavorite: Boolean, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            // Hanya update database lokal (Room) melalui repository
            repository?.updateEvent(event.copy(isFavorite = isFavorite))

            withContext(Dispatchers.Main) {
                val message = if (isFavorite) context.getString(R.string.added_to_favorites) else context.getString(R.string.removed_from_favorites)
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }
    // ------------------------------------------

    fun resetForm(context: Context) {
        eventName = ""
        dateTime = ""
        location = ""
        organizer = ""
        platformLink = ""
        description = ""
        imageUri = null
        imageUrl = null
        eventUserId = null
        eventType = context.getString(R.string.free_event)
        price = ""
    }

    private fun validateForm(isUpdate: Boolean = false, context: Context): Boolean {
        error = null
        return when {
            !isUpdate && imageUri == null -> {
                error = context.getString(R.string.event_poster_empty_error)
                false
            }
            isUpdate && imageUrl.isNullOrBlank() && imageUri == null -> {
                error = context.getString(R.string.event_poster_empty_error)
                false
            }
            eventName.isBlank() -> {
                error = context.getString(R.string.event_name_empty_error)
                false
            }
            dateTime.isBlank() -> {
                error = context.getString(R.string.date_time_empty_error)
                false
            }
            location.isBlank() -> {
                error = context.getString(R.string.location_empty_error)
                false
            }
            organizer.isBlank() -> {
                error = context.getString(R.string.organizer_empty_error)
                false
            }
            eventType == context.getString(R.string.paid_event) && price.isBlank() -> {
                error = context.getString(R.string.price_empty_error)
                false
            }
            else -> true
        }
    }

    fun loadEvent(eventId: String, context: Context) {
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
                    imageUrl = event.imageUrl
                    eventType = event.eventType
                    price = event.price
                    eventUserId = event.userId
                } ?: run {
                    error = context.getString(R.string.event_not_found_error)
                }
            } catch (e: Exception) {
                error = context.getString(R.string.load_event_failed_error, e.message)
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
        if (!validateForm(isUpdate = false, context = context)) {
            onError(error!!)
            return
        }

        val currentUser = auth.currentUser
        if (currentUser == null) {
            onError(context.getString(R.string.user_login_required_error))
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
                    imageUrl = imageUrl,
                    userId = currentUser.uid,
                    eventType = eventType,
                    price = if (eventType == context.getString(R.string.free_event)) context.getString(R.string.free_event) else price
                )

                saveEventToFirestore(event)

                analytics.logEvent("event_created") {
                    param("event_name", eventName)
                    param("event_type", eventType)
                    param("event_location", location)
                }
                onSuccess()
            } catch (e: Exception) {
                error = e.message ?: context.getString(R.string.create_event_failed_error)
                onError(error!!)
            } finally {
                isLoading = false
            }
        }
    }

    fun updateEvent(
        context: Context,
        eventId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (!validateForm(isUpdate = true, context = context)) {
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
                    imageUrl ?: throw IllegalStateException(context.getString(R.string.image_url_null_error))
                }

                val eventData = mapOf(
                    "title" to eventName,
                    "description" to description,
                    "dateTime" to dateTime,
                    "location" to location,
                    "organizer" to organizer,
                    "platformLink" to platformLink,
                    "imageUrl" to finalImageUrl,
                    "eventType" to eventType,
                    "price" to if (eventType == context.getString(R.string.free_event)) context.getString(R.string.free_event) else price
                )

                firestore.collection("events").document(eventId).update(eventData).await()
                onSuccess()
            } catch (e: Exception) {
                error = e.message ?: context.getString(R.string.update_event_failed_error)
                onError(error!!)
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteEvent(
        eventId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        context: Context
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
                error = e.message ?: context.getString(R.string.delete_event_failed_error)
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
            return response.body()?.data?.link ?: throw Exception(context.getString(R.string.image_link_not_found_error))
        } else {
            throw Exception(response.message() ?: context.getString(R.string.image_upload_failed_error))
        }
    }

    private suspend fun saveEventToFirestore(event: Event) {
        firestore.collection("events").add(event).await()
    }
}