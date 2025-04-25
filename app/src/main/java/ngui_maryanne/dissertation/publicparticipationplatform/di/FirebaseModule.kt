package ngui_maryanne.dissertation.publicparticipationplatform.di

import ngui_maryanne.dissertation.publicparticipationplatform.repositories.blockchainrepo.BlockChainRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.blockchainrepo.BlockChainRepositoryImpl
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.officialsrepo.OfficialsRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.officialsrepo.OfficialsRepositoryImpl
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.citizenrepo.CitizenRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.citizenrepo.CitizenRepositoryImpl
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.commentrepo.CommentRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.commentrepo.CommentRepositoryImpl
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.nationaldbrepo.NationalDBRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.nationaldbrepo.NationalDBRepositoryImpl
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.policyrepo.PolicyRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.policyrepo.PolicyRepositoryImpl
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.pollsrepo.PollsRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.pollsrepo.PollsRepositoryImpl
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.storagerepo.StorageRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.storagerepo.StorageRepositoryImpl
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
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.budgetrepo.BudgetRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.budgetrepo.BudgetRepositoryImpl
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.petitionrepo.PetitionRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.petitionrepo.PetitionRepositoryImpl
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

    @Provides
    @Singleton
    fun providePetitionRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
    ): PetitionRepository {
        return PetitionRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideBudgetRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
    ): BudgetRepository {
        return BudgetRepositoryImpl(firestore)
    }

}
