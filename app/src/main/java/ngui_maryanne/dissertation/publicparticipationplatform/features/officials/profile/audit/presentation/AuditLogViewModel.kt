package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.profile.audit.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.auditlogrepo.AuditLogRepository
import javax.inject.Inject

@HiltViewModel
class OfficialAuditLogViewModel @Inject constructor(
    private val repo: AuditLogRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _state = mutableStateOf(OfficialAuditLogState())
    val state: State<OfficialAuditLogState> = _state

    init {
        onEvent(OfficialAuditLogEvent.LoadLogs)
    }

    fun onEvent(event: OfficialAuditLogEvent) {
        when (event) {
            is OfficialAuditLogEvent.LoadLogs -> {
                repo.getMyAuditLogsRealtime(auth.currentUser!!.uid) { logs ->
                    if (logs.isNotEmpty()) {
                        _state.value = _state.value.copy(logs = logs.map { OfficialAuditLogUIModel(it) })
                    }
                }

            }

            is OfficialAuditLogEvent.RevealUser -> {
                viewModelScope.launch {
                    val (name, type, profileImage) = repo.getUserDetails(event.userId)
                    val updated = _state.value.logs.toMutableList()
                    val target = updated[event.index]
                    updated[event.index] = target.copy(revealedName = name, userType = type)
                    _state.value = _state.value.copy(logs = updated)
                }
            }

            is OfficialAuditLogEvent.RunDiscrepancyCheck -> {
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
                    discrepancyFound = issues.isNotEmpty(),
                    showDiscrepancyDialog = true
                )
            }

            is OfficialAuditLogEvent.DismissDiscrepancyDialog -> {
                _state.value = _state.value.copy(showDiscrepancyDialog = false)
            }
        }
    }
}
