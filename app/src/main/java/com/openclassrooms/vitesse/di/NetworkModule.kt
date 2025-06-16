package com.openclassrooms.vitesse.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // Provides a singleton instance of Retrofit for network communication
    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://192.168.1.20:8080/") // use this address on emulation : 10.0.2.2:8080
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                )
            )
            .client(provideOkHttpClient()) // Uses a separate function for OkHttpClient configuration
            .build()
    }


    // Provides a singleton instance of LoginClient using Retrofit
    @Singleton
    @Provides
    fun provideLoginClient(retrofit: Retrofit): AuraClient {
        return retrofit.create(AuraClient::class.java)
    }

    // Private function to configure OkHttpClient with an interceptor for logging
    private fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        }.build()
    }
}