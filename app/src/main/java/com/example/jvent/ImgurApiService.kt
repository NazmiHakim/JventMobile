package com.example.jvent

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ImgurApiService {
    @Multipart
    @POST("image")
    suspend fun uploadImage(
        @Header("Authorization") authHeader: String,
        @Part image: MultipartBody.Part,
        @Part("title") title: RequestBody?,
        @Part("description") description: RequestBody?
    ): Response<ImgurResponse>
}