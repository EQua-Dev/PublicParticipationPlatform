package ngui_maryanne.dissertation.publicparticipationplatform.di

import android.util.Log
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
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.admindashboardrepo.AdminDashboardRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.admindashboardrepo.AdminDashboardRepositoryImpl
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.announcementrepo.AnnouncementRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.announcementrepo.AnnouncementRepositoryImpl
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.auditlogrepo.AuditLogRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.auditlogrepo.AuditLogRepositoryImpl
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.budgetrepo.BudgetRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.budgetrepo.BudgetRepositoryImpl
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.notificationrepo.NotificationRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.notificationrepo.NotificationRepositoryImpl
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.petitionrepo.PetitionRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.petitionrepo.PetitionRepositoryImpl
import ngui_maryanne.dissertation.publicparticipationplatform.utils.LocationUtils
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
    fun provideTranslatorProvider(): TranslatorProvider = TranslatorProvider()

    @Provides
    @Singleton
    fun provideOfficialsRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        storage: FirebaseStorage,
        blockchain: BlockChainRepository
    ): OfficialsRepository {
        return OfficialsRepositoryImpl(auth, firestore, storage, blockchain)
    }

    @Provides
    @Singleton
    fun provideCitizensRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        storage: FirebaseStorage,
        storageRepository: StorageRepository,
        blockchain: BlockChainRepository
    ): CitizenRepository {
        return CitizenRepositoryImpl(auth, firestore, storage, storageRepository, blockchain)
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
        auth: FirebaseAuth,
        locationUtils: LocationUtils
    ): BlockChainRepository {
        return BlockChainRepositoryImpl(firestore, auth, locationUtils)
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
        blockchain: BlockChainRepository,
        translator: TranslatorProvider
    ): PolicyRepository {
        return PolicyRepositoryImpl(
            blockchain, firestore, auth, translator
        )
    }

    @Provides
    @Singleton
    fun providePollsRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        blockchain: BlockChainRepository, translator: TranslatorProvider
    ): PollsRepository {
        return PollsRepositoryImpl(firestore, auth, blockchain, translator)
    }

    @Provides
    @Singleton
    fun provideCommentRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        blockchain: BlockChainRepository
    ): CommentRepository {
        return CommentRepositoryImpl(firestore, blockchain)
    }

    @Provides
    @Singleton
    fun providePetitionRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        blockchain: BlockChainRepository
    ): PetitionRepository {
        return PetitionRepositoryImpl(firestore, blockchain)
    }

    @Provides
    @Singleton
    fun provideBudgetRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        blockchain: BlockChainRepository,
        translator: TranslatorProvider
    ): BudgetRepository {
        return BudgetRepositoryImpl(firestore, blockchain, translator)
    }

    @Provides
    @Singleton
    fun provideAuditLogsRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        blockchain: BlockChainRepository
    ): AuditLogRepository {
        return AuditLogRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideAdminDashboardRepository(
        firestore: FirebaseFirestore,
    ): AdminDashboardRepository {
        return AdminDashboardRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(
        firestore: FirebaseFirestore,
    ): NotificationRepository {
        return NotificationRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideAnnouncementRepository(
        firestore: FirebaseFirestore,
    ): AnnouncementRepository {
        return AnnouncementRepositoryImpl(firestore)
    }

}
