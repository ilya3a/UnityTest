package com.yoyo.concurrenteventtracker.network

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import javax.inject.Singleton

@Singleton
interface AnalyticsUploadApi {
    @Multipart
    @POST("files/upload ")
    suspend fun uploadCompressedFile(
        @Part file: MultipartBody.Part
    ): Response<Unit>
}