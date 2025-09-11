package com.example.mobilecomputingassignment.core.di

import com.example.mobilecomputingassignment.data.remote.SquiggleApiService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

  @Provides
  @Singleton
  fun provideFirebaseAuth(): FirebaseAuth {
    return FirebaseAuth.getInstance()
  }

  @Provides
  @Singleton
  fun provideFirebaseFirestore(): FirebaseFirestore {
    return FirebaseFirestore.getInstance()
  }

  @Provides
  @Singleton
  fun provideOkHttpClient(): OkHttpClient {
    val loggingInterceptor = HttpLoggingInterceptor().apply {
      level = HttpLoggingInterceptor.Level.BODY
    }
    
    return OkHttpClient.Builder()
            .addInterceptor { chain ->
              val original = chain.request()
              val requestBuilder = original.newBuilder()
                      .header("User-Agent", "WatchMatesApp/1.0 (Android)")
              val request = requestBuilder.build()
              chain.proceed(request)
            }
            .addInterceptor(loggingInterceptor)
            .build()
  }

  @Provides
  @Singleton
  fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
            .baseUrl("https://api.squiggle.com.au/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
  }

  @Provides
  @Singleton
  fun provideSquiggleApiService(retrofit: Retrofit): SquiggleApiService {
    return retrofit.create(SquiggleApiService::class.java)
  }
}
