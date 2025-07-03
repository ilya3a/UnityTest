package com.yoyo.concurrenteventtracker.network.model

internal data class UploadResponse(
    val originalname: String,
    val filename: String,
    val location: String
)