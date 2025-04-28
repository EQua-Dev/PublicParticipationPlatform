package ngui_maryanne.dissertation.publicparticipationplatform.repositories.admindashboardrepo

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.flow.Flow
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.BUDGETS_REF
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.PETITIONS_REF
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.POLICIES_REF
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.POLLS_REF
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.REGISTERED_CITIZENS_REF
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine

class AdminDashboardRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore) :
    AdminDashboardRepository {

    override suspend fun getCitizenCount(): Flow<Int> = getCollectionCountRealTime(REGISTERED_CITIZENS_REF)
    override suspend fun getPoliciesCount(): Flow<Int> = getCollectionCountRealTime(POLICIES_REF)
    override suspend fun getPollsCount(): Flow<Int> = getCollectionCountRealTime(POLLS_REF)
    override suspend fun getBudgetsCount(): Flow<Int> = getCollectionCountRealTime(BUDGETS_REF)
    override suspend fun getPetitionsCount(): Flow<Int> = getCollectionCountRealTime(PETITIONS_REF)

    private fun getCollectionCountRealTime(collectionPath: String): Flow<Int> = callbackFlow {
        val listenerRegistration = firestore.collection(collectionPath)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)  // Close the flow with error
                    return@addSnapshotListener
                }
                trySend(snapshot?.size() ?: 0)  // Emit the size
            }

        awaitClose {
            listenerRegistration.remove()  // Very important! Remove listener to avoid memory leaks
        }
    }
}