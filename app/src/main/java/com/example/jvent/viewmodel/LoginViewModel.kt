package com.example.jvent.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jvent.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var isLoading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    fun updateEmail(newEmail: String) {
        email = newEmail
        error = null
    }

    fun updatePassword(newPassword: String) {
        password = newPassword
        error = null
    }

    private fun validateForm(): Boolean {
        return when {
            email.isBlank() -> {
                error = "Email cannot be empty"
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                error = "Please enter a valid email"
                false
            }
            password.isBlank() -> {
                error = "Password cannot be empty"
                false
            }
            password.length < 6 -> {
                error = "Password must be at least 6 characters"
                false
            }
            else -> true
        }
    }

    fun login(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (!validateForm()) {
            onError(error ?: "Invalid form")
            return
        }

        viewModelScope.launch {
            isLoading = true
            error = null

            authRepository.login(email, password)
                .onSuccess {
                    onSuccess()
                }
                .onFailure { e ->
                    error = e.message ?: "Login failed"
                    onError(error!!)
                }

            isLoading = false
        }
    }
}