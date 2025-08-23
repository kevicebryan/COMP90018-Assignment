package com.example.mobilecomputingassignment.core.di

import com.example.mobilecomputingassignment.data.remote.SquiggleApiService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
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
  fun provideRetrofit(): Retrofit {
    return Retrofit.Builder()
            .baseUrl("https://api.squiggle.com.au/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
  }

  @Provides
  @Singleton
  fun provideSquiggleApiService(retrofit: Retrofit): SquiggleApiService {
    return retrofit.create(SquiggleApiService::class.java)
  }
}
