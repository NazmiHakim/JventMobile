package com.example.jvent.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jvent.ImgurApiClient
import com.example.jvent.model.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
    private val imgurApiService = ImgurApiClient.apiService

    var eventName by mutableStateOf("")
    var ticketCategory by mutableStateOf("")
    var dateTime by mutableStateOf("")
    var location by mutableStateOf("")
    var organizer by mutableStateOf("")
    var platformLink by mutableStateOf("")
    var description by mutableStateOf("")
    var imageUri by mutableStateOf<Uri?>(null)
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    fun resetForm() {
        eventName = ""
        ticketCategory = ""
        dateTime = ""
        location = ""
        organizer = ""
        platformLink = ""
        description = ""
        imageUri = null
    }

    private fun validateForm(): Boolean {
        return when {
            eventName.isBlank() -> {
                error = "Event name cannot be empty"
                false
            }
            dateTime.isBlank() -> {
                error = "Date and time cannot be empty"
                false
            }
            location.isBlank() -> {
                error = "Location cannot be empty"
                false
            }
            imageUri == null -> {
                error = "Please select an image"
                false
            }
            else -> true
        }
    }

    fun createEvent(
        context: android.content.Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (FirebaseAuth.getInstance().currentUser == null) {
            onError("User must be logged in")
            return
        }

        viewModelScope.launch {
            isLoading = true
            error = null

            try {
                // Upload gambar ke Imgur
                val imageUrl = uploadImageToImgur(
                    imageUri = imageUri!!,
                    title = eventName,
                    description = description,
                    clientId = "cff49ba6c9e160f",
                    context = context
                )

                // Simpan event ke Firestore
                val event = Event(
                    title = eventName,
                    description = description,
                    dateTime = dateTime,
                    location = location,
                    organizer = organizer,
                    platformLink = platformLink,
                    ticketCategory = ticketCategory,
                    imageUrl = imageUrl
                )

                saveEventToFirestore(event)
                onSuccess()
            } catch (e: Exception) {
                error = e.message ?: "Failed to create event"
                onError(error!!)
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
        context: android.content.Context // Add context parameter
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