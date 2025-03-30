package awesomenessstudios.schoolprojects.publicparticipationplatform.di

import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.blockchainrepo.BlockChainRepository
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.blockchainrepo.BlockChainRepositoryImpl
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.officialsrepo.OfficialsRepository
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.officialsrepo.OfficialsRepositoryImpl
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.citizenrepo.CitizenRepository
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.citizenrepo.CitizenRepositoryImpl
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.commentrepo.CommentRepository
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.commentrepo.CommentRepositoryImpl
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.nationaldbrepo.NationalDBRepository
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.nationaldbrepo.NationalDBRepositoryImpl
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.policyrepo.PolicyRepository
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.policyrepo.PolicyRepositoryImpl
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.pollsrepo.PollsRepository
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.pollsrepo.PollsRepositoryImpl
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.storagerepo.StorageRepository
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.storagerepo.StorageRepositoryImpl
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
        storageRepository: StorageRepository
    ): CitizenRepository {
        return CitizenRepositoryImpl(auth, firestore, storage, storageRepository)
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
        auth: FirebaseAuth,
    ): PolicyRepository {
        return PolicyRepositoryImpl(firestore, auth)
    }

    @Provides
    @Singleton
    fun providePollsRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
    ): PollsRepository {
        return PollsRepositoryImpl(firestore, auth)
    }

    @Provides
    @Singleton
    fun provideCommentRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
    ): CommentRepository {
        return CommentRepositoryImpl(firestore)
    }

}
