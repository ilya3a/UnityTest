package com.yoyo.concurrenteventtracker.network

import com.yoyo.concurrenteventtracker.BuildConfig
import com.yoyo.concurrenteventtracker.network.model.UploadResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import javax.inject.Singleton

@Singleton
internal interface AnalyticsUploadApi {
    @Multipart
    @POST(BuildConfig.UPLOAD_END_POINT)
    suspend fun uploadCompressedFile(
        @Part file: MultipartBody.Part
    ): Response<UploadResponse>
}