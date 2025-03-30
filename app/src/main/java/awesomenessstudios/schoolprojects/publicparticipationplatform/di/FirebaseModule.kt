package awesomenessstudios.schoolprojects.publicparticipationplatform.di

import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.BlockChainRepository
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.BlockChainRepositoryImpl
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.OfficialsRepository
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.OfficialsRepositoryImpl
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.CitizenRepository
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.CitizenRepositoryImpl
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.NationalDBRepository
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.NationalDBRepositoryImpl
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.PolicyRepository
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.PolicyRepositoryImpl
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.StorageRepository
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.StorageRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        val firestore = Firebase.firestore
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        return firestore
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideOfficialsRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        storage: FirebaseStorage
    ): OfficialsRepository {
        return OfficialsRepositoryImpl(auth, firestore, storage)
    }

    @Provides
    @Singleton
    fun provideCitizensRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        storage: FirebaseStorage,
        blockchain: BlockChainRepository
    ): CitizenRepository {
        return CitizenRepositoryImpl(auth, firestore, storage, blockchain)
    }

    @Provides
    @Singleton
    fun provideNationalDBRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        storage: FirebaseStorage,
        blockchain: BlockChainRepository
    ): NationalDBRepository {
        return NationalDBRepositoryImpl(auth, firestore, storage, blockchain)
    }

    @Provides
    @Singleton
    fun provideBlockChainRepository(
        firestore: FirebaseFirestore,
    ): BlockChainRepository {
        return BlockChainRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideStorage(
        storage: FirebaseStorage,
    ): StorageRepository {
        return StorageRepositoryImpl(storage)
    }

    @Provides
    @Singleton
    fun providePolicyRepository(
        firestore: FirebaseFirestore,
    ): PolicyRepository {
        return PolicyRepositoryImpl(firestore)
    }

}
