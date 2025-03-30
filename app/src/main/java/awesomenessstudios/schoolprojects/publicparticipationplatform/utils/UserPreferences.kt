package awesomenessstudios.schoolprojects.publicparticipationplatform.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import awesomenessstudios.schoolprojects.publicparticipationplatform.data.enums.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferences(private val context: Context) {
    companion object {
        val ROLE_KEY = stringPreferencesKey("user_role")
        val USER_ID = stringPreferencesKey("user_id")
    }

    // Save the selected role
    suspend fun saveRole(role: UserRole) {
        context.dataStore.edit { preferences ->
            preferences[ROLE_KEY] = role.name // Store the enum name as a String
        }
    }

    // Retrieve the selected role
    val role: Flow<UserRole> = context.dataStore.data
        .map { preferences ->
            preferences[ROLE_KEY]?.let { roleName ->
                UserRole.valueOf(roleName) // Convert the stored String back to UserRole
            } ?: UserRole.UNKNOWN // Default to UNKNOWN if no role is stored
        }

    // Save the selected role
    suspend fun saveUserId(userId: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = userId // Store the enum name as a String
        }
    }

    // Retrieve the selected role
    val loggedInUserId: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[USER_ID] ?: "" // Default to UNKNOWN if no role is stored
        }
}