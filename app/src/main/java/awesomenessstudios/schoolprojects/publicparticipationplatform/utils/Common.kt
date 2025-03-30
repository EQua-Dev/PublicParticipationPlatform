/*
 * Copyright (c) 2024.
 * Luomy EQua
 * Under Awesomeness Studios
 */

package awesomenessstudios.schoolprojects.publicparticipationplatform.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object Common {

    val mAuth = FirebaseAuth.getInstance()
    val fireStoreDB = Firebase.firestore.batch()
//    val db = Firebase.firestore



    const val LOCATION_PERMISSION_REQUEST_CODE = 1001


    private const val TEACHERS_REF = "Buzor Platform Teachers"
    private const val STUDENTS_REF = "Buzor Platform Students"
    private const val WALLETS_REF = "Buzor Platform Wallets"
//    private const val QUIZ_REF = "MindSpark Quizzes"
//    private const val VIDEOS_REF = "MindSpark Videos"
//    private const val OUTDOOR_TASKS_REF = "MindSpark Outdoor Tasks"
//    private const val SCORES_REF = "MindSpark Scores"


    val teachersCollectionRef = Firebase.firestore.collection(TEACHERS_REF)
    val studentsCollectionRef = Firebase.firestore.collection(STUDENTS_REF)
    val walletsCollectionRef = Firebase.firestore.collection(WALLETS_REF)
//    val quizCollectionRef = Firebase.firestore.collection(QUIZ_REF)
//    val videoCollectionRef = Firebase.firestore.collection(VIDEOS_REF)
//    val outdoorTaskCollectionRef = Firebase.firestore.collection(OUTDOOR_TASKS_REF)
//    val scoresCollectionRef = Firebase.firestore.collection(SCORES_REF)


    fun logout() {
        mAuth.signOut()
    }


}
