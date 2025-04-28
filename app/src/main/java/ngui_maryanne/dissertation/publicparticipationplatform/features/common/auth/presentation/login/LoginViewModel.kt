package ngui_maryanne.dissertation.publicparticipationplatform.features.common.auth.presentation.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Common.mAuth
import ngui_maryanne.dissertation.publicparticipationplatform.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.TransactionTypes
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.blockchainrepo.BlockChainRepository
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val blockChainRepository: BlockChainRepository
) : ViewModel() {
    private val _state = mutableStateOf(LoginState())
    val state: State<LoginState> = _state

    init {
        viewModelScope.launch {
            userPreferences.role.collect { role ->
                if (role != null) {
                    _state.value = _state.value.copy(
                        userRole = role.name
                    )
                }
            }
        }
    }

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                _state.value = _state.value.copy(email = event.email)
            }

            is LoginEvent.PasswordChanged -> {
                _state.value = _state.value.copy(password = event.password)
            }

            LoginEvent.Login -> {
                loginUser()
            }
        }
    }

    private fun loginUser() {
        _state.value = _state.value.copy(isLoading = true, errorMessage = null)

        val email = _state.value.email
        val password = _state.value.password
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        viewModelScope.launch {
                            blockChainRepository.createBlockchainTransaction(
                                TransactionTypes.LOGIN
                            )
                        }

                        _state.value = _state.value.copy(
                            isLoading = false,
                            isLoginSuccessful = true
                        )
                    } else {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            errorMessage = task.exception?.message ?: "Login failed"
                        )
                    }
                }


    }
}