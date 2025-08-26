package com.example.mobilecomputingassignment.core.di

import com.example.mobilecomputingassignment.data.remote.firebase.EventFirestoreService
import com.example.mobilecomputingassignment.data.remote.firebase.FirestoreService
import com.example.mobilecomputingassignment.data.repository.EventRepository
import com.example.mobilecomputingassignment.data.repository.MatchRepository
import com.example.mobilecomputingassignment.data.repository.UserRepository
import com.example.mobilecomputingassignment.data.remote.SquiggleApiService
import com.example.mobilecomputingassignment.domain.repository.IEventRepository
import com.example.mobilecomputingassignment.domain.repository.IUserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(firestoreService: FirestoreService): IUserRepository {
        return UserRepository(firestoreService)
    }

    @Provides
    @Singleton
    fun provideEventRepository(eventFirestoreService: EventFirestoreService): IEventRepository {
        return EventRepository(eventFirestoreService)
    }

    @Provides
    @Singleton
    fun provideMatchRepository(squiggleApiService: SquiggleApiService): MatchRepository {
        return MatchRepository(squiggleApiService)
    }
}
