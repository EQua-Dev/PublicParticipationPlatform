package ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.audit.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.auditlogrepo.AuditLogRepository
import java.security.MessageDigest
import javax.inject.Inject

@HiltViewModel
class SuperAdminAuditLogViewModel @Inject constructor(private val repo: AuditLogRepository) : ViewModel() {

    private val _state = MutableStateFlow(SuperAdminAuditLogState())
    val state: StateFlow<SuperAdminAuditLogState> = _state.asStateFlow()

    init {
        onEvent(SuperAdminAuditLogEvent.LoadLogs)
    }

    fun onEvent(event: SuperAdminAuditLogEvent) {
        when (event) {
            is SuperAdminAuditLogEvent.LoadLogs -> {
                repo.getAuditLogsRealtime { logs ->
                    _state.value = _state.value.copy(logs = logs.map { SuperAdminAuditLogUIModel(it) })
                }
            }

            is SuperAdminAuditLogEvent.RevealUser -> {
                viewModelScope.launch {
                    val (name, type, profileImage) = repo.getUserDetails(event.userId)
                    val updated = _state.value.logs.map { logUI ->
                        if (logUI.log.transactionId == event.logId) {
                            logUI.copy(revealedName = name, userType = type, profileImage = profileImage)
                        } else logUI
                    }
                    _state.value = _state.value.copy(logs = updated)
                }
            }


            is SuperAdminAuditLogEvent.RunDiscrepancyCheck -> {
                val logs = _state.value.logs.map { it.log }
                val issues = mutableListOf<String>()

                for (i in 1 until logs.size) {
                    val prevLog = logs[i - 1]
                    val expectedHash = with(prevLog) {
                        val input = transactionId + timestamp + previousHash + createdBy
                        val digest = MessageDigest.getInstance("SHA-256")
                        val hashBytes = digest.digest(input.toByteArray())
                        hashBytes.joinToString("") { "%02x".format(it) }
                    }

                    if (logs[i].previousHash != expectedHash) {
                        issues.add("Discrepancy at index $i")
                    }
                }

                _state.value = _state.value.copy(
                    discrepancies = issues,
                    discrepancyFound = issues.isNotEmpty(),
                    showDiscrepancyDialog = true
                )
            }

            is SuperAdminAuditLogEvent.DismissDiscrepancyDialog -> {
                _state.value = _state.value.copy(showDiscrepancyDialog = false)
            }
        }
    }
}
