package com.example.mobilecomputingassignment.di

import android.content.Context
import com.example.mobilecomputingassignment.data.repository.DefaultLocationRepository
import com.example.mobilecomputingassignment.domain.repository.LocationRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

  @Provides
  @Singleton
  fun provideFusedLocationProviderClient(
          @ApplicationContext context: Context
  ): FusedLocationProviderClient {
    return LocationServices.getFusedLocationProviderClient(context)
  }

  @Provides
  @Singleton
  fun provideLocationRepository(
          @ApplicationContext context: Context,
          fusedLocationClient: FusedLocationProviderClient
  ): LocationRepository {
    return DefaultLocationRepository(context, fusedLocationClient)
  }
}
