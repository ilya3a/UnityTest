package com.yoyo.concurrenteventtracker.network

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.yoyo.concurrenteventtracker.data.db.AnalyticsEvent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.GZIPOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsUploadImpl @Inject constructor(
    private val api: AnalyticsUploadApi,
    @param:ApplicationContext private val context: Context
) : AnalyticsApi {

    /**
     * Sends a list of analytics events to the server.
     */
    override suspend fun send(events: List<AnalyticsEvent>): Boolean {
        if (events.isEmpty()) return true

        return try {
            // 1. Convert to JSON
            val json = Gson().toJson(events)

            // 2. Save to .json
            val jsonFile = File(context.cacheDir, "events.json")
            jsonFile.writeText(json)

            // 3. GZIP compress
            val gzipFile = File(context.cacheDir, "events.json.gz")
            GZIPOutputStream(FileOutputStream(gzipFile)).use { gzipOut ->
                FileInputStream(jsonFile).use { input ->
                    input.copyTo(gzipOut)
                }
            }

            // 4. Prepare multipart
            val filePart = MultipartBody.Part.createFormData(
                name = "file",
                filename = gzipFile.name,
                body = gzipFile.asRequestBody("application/gzip".toMediaTypeOrNull())
            )

            // 5. Upload via API
            val response = api.uploadCompressedFile(filePart)
            Log.d("Upload", "Response code: ${response.code()}")
            Log.d("Upload", "Response body: ${response.body().toString()}")
            val success = response.isSuccessful


            if (success) {
                Log.d("Upload", "Response code: ${response.code()}")
                // 6. Cleanup
                jsonFile.delete()
                gzipFile.delete()
            }

            success
        } catch (e: Exception) {
            Log.e("AnalyticsUploadImpl", "Upload failed", e)
            false
        }
    }
}