package awesomenessstudios.schoolprojects.publicparticipationplatform.repositories

import android.util.Log
import awesomenessstudios.schoolprojects.publicparticipationplatform.data.models.Policy
import awesomenessstudios.schoolprojects.publicparticipationplatform.utils.Constants
import awesomenessstudios.schoolprojects.publicparticipationplatform.utils.Constants.POLICIES_REF
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PolicyRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : PolicyRepository {

    override suspend fun getAllPolicies(): List<Policy> {
        return try {
            firestore.collection(POLICIES_REF)
                .orderBy("dateCreated", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(Policy::class.java)
        } catch (e: Exception) {
            throw Exception("Failed to fetch policies: ${e.message}")
        }
    }

    override suspend fun createPolicy(policy: Policy) {
        Log.d("PRI", "createPolicy: Creating...")
        try {
            firestore.collection(POLICIES_REF)
                .document(policy.id)
                .set(policy)
                .await()
        } catch (e: Exception) {
            throw Exception("Failed to create policy: ${e.message}")
        }
    }
}