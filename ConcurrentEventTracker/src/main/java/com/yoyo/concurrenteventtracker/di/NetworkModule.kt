package com.yoyo.concurrenteventtracker.di

import android.content.Context
import com.yoyo.concurrenteventtracker.BuildConfig
import com.yoyo.concurrenteventtracker.network.AnalyticsApi
import com.yoyo.concurrenteventtracker.network.AnalyticsUploadApi
import com.yoyo.concurrenteventtracker.network.AnalyticsUploadImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

//    @Provides
//    @Singleton
//    fun provideApiService(): AnalyticsApi {
//        return MockAnalyticsApi()
//    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.UPLOAD_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAnalyticsUploadApi(@ApplicationContext context: Context, retrofit: Retrofit): AnalyticsApi {
        val retrofit = retrofit.create(AnalyticsUploadApi::class.java)
        return AnalyticsUploadImpl(retrofit, context)
    }
}