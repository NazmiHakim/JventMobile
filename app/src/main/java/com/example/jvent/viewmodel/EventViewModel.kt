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

    // + Tambahkan state untuk field baru
    var eventType by mutableStateOf("Gratis") // Opsi: "Gratis" atau "Berbayar"
    var price by mutableStateOf("")

    fun resetForm() {
        eventName = ""
        ticketCategory = ""
        dateTime = ""
        location = ""
        organizer = ""
        platformLink = ""
        description = ""
        imageUri = null
        // + Reset state baru
        eventType = "Gratis"
        price = ""
    }

    // * Perbarui validasi
    private fun validateForm(): Boolean {
        error = null // Reset error sebelum validasi
        return when {
            imageUri == null -> {
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

    fun createEvent(
        context: android.content.Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // * Gunakan validasi yang sudah diperbarui
        if (!validateForm()) {
            onError(error!!)
            return
        }

        if (FirebaseAuth.getInstance().currentUser == null) {
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

                // * Sertakan field baru saat membuat objek Event
                val event = Event(
                    title = eventName,
                    description = description,
                    dateTime = dateTime,
                    location = location,
                    organizer = organizer,
                    platformLink = platformLink,
                    ticketCategory = ticketCategory,
                    imageUrl = imageUrl,
                    eventType = eventType, // + Tambahkan
                    price = if (eventType == "Gratis") "Gratis" else price // + Tambahkan
                )

                saveEventToFirestore(event)
                onSuccess()
            } catch (e: Exception) {
                error = e.message ?: "Gagal membuat event"
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
        context: android.content.Context
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