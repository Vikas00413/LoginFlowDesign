package com.example.loginflow.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginflow.core.auth.security.TokenManager
import com.example.loginflow.core.database.dao.SessionDao
import com.example.loginflow.core.database.dao.UserDao
import com.example.loginflow.core.database.entity.SessionEntity
import com.example.loginflow.core.database.entity.UserEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DebugDataState(
    val isLoading: Boolean = true,
    val users: List<UserEntity> = emptyList(),
    val sessions: List<SessionEntity> = emptyList(),
    val tokenSnapshot: com.example.loginflow.core.auth.security.TokenStorageSnapshot? = null,
    val error: String? = null
)

@HiltViewModel
class DebugDataViewModel @Inject constructor(
    private val userDao: UserDao,
    private val sessionDao: SessionDao,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(DebugDataState())
    val state: StateFlow<DebugDataState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val user = userDao.getUser()
                val session = sessionDao.getSession()
                _state.update {
                    it.copy(
                        isLoading = false,
                        users = listOfNotNull(user),
                        sessions = listOfNotNull(session),
                        tokenSnapshot = tokenManager.readStorageSnapshot()
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, error = e.message ?: "Failed to load debug data")
                }
            }
        }
    }
}
