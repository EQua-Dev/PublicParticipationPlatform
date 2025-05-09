package ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.audit.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.auditlogrepo.AuditLogRepository
import javax.inject.Inject

@HiltViewModel
class SuperAdminAuditLogViewModel @Inject constructor(private val repo: AuditLogRepository) : ViewModel() {

    private val _state = mutableStateOf(SuperAdminAuditLogState())
    val state: State<SuperAdminAuditLogState> = _state

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
                    val updated = _state.value.logs.toMutableList()
                    val target = updated[event.index]
                    updated[event.index] = target.copy(revealedName = name, userType = type)
                    _state.value = _state.value.copy(logs = updated)
                }
            }

            is SuperAdminAuditLogEvent.RunDiscrepancyCheck -> {
                val logs = _state.value.logs.map { it.log }
                val issues = mutableListOf<String>()
                for (i in 1 until logs.size) {
                    val expectedHash = (logs[i - 1].transactionId + logs[i - 1].timestamp +
                            logs[i - 1].previousHash + logs[i - 1].createdBy).hashCode().toString()

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
