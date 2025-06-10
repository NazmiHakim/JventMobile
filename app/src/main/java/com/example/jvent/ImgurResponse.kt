package com.example.jvent

data class ImgurResponse(
    val data: ImgurData?,
    val success: Boolean,
    val status: Int
)

data class ImgurData(
    val id: String?,
    val title: String?,
    val description: String?,
    val datetime: Int?,
    val type: String?,
    val animated: Boolean?,
    val width: Int?,
    val height: Int?,
    val size: Int?,
    val views: Int?,
    val bandwidth: Int?,
    val vote: String?,
    val favorite: Boolean?,
    val nsfw: Boolean?,
    val section: String?,
    val account_url: String?,
    val account_id: Int?,
    val is_ad: Boolean?,
    val in_most_viral: Boolean?,
    val has_sound: Boolean?,
    val tags: List<String>?,
    val ad_type: Int?,
    val ad_url: String?,
    val edited: String?,
    val in_gallery: Boolean?,
    val deletehash: String?,
    val name: String?,
    val link: String?
)