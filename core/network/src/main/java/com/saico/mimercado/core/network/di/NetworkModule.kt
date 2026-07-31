package com.saico.mimercado.core.network.di

import com.google.firebase.firestore.FirebaseFirestore
import com.saico.mimercado.core.network.api.OffApiService
import com.saico.mimercado.core.network.api.USDAFoodDataService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.HEADERS
        }

        val userAgentInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", "MiMercadoApp - Android - contact@saico.com")
                .build()
            chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(userAgentInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named("USDARetrofit")
    fun provideUSDARetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.nal.usda.gov/fdc/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("OFFRetrofit")
    fun provideOFFRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://world.openfoodfacts.org/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideUSDAFoodDataService(@Named("USDARetrofit") retrofit: Retrofit): USDAFoodDataService {
        return retrofit.create(USDAFoodDataService::class.java)
    }

    @Provides
    @Singleton
    fun provideOffApiService(@Named("OFFRetrofit") retrofit: Retrofit): OffApiService {
        return retrofit.create(OffApiService::class.java)
    }
}
