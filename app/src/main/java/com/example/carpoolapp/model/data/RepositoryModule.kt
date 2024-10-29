package com.example.carpoolapp.model.data

import com.example.carpoolapp.model.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
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
    fun provideFirebaseFirestore() : FirebaseFirestore{
        return FirebaseFirestore.getInstance()
    }
    @Provides
    @Singleton
    fun provideUserRepository(firestore: FirebaseFirestore) : UserRepository{
        return UserRepository(firestore)
    }
}
