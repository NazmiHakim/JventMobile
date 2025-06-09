package com.example.jvent.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jvent.repository.AuthRepository
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {
    var username by mutableStateOf("")
        private set
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var confirmPassword by mutableStateOf("")
        private set
    var isLoading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    fun updateUsername(newUsername: String) {
        username = newUsername
        error = null
    }

    fun updateEmail(newEmail: String) {
        email = newEmail
        error = null
    }

    fun updatePassword(newPassword: String) {
        password = newPassword
        error = null
    }

    fun updateConfirmPassword(newConfirmPassword: String) {
        confirmPassword = newConfirmPassword
        error = null
    }

    private fun validateForm(): Boolean {
        return when {
            username.isBlank() -> {
                error = "Username cannot be empty"
                false
            }
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
            confirmPassword != password -> {
                error = "Passwords do not match"
                false
            }
            else -> true
        }
    }

    fun register(
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

            authRepository.register(email, password, username)
                .onSuccess {
                    onSuccess()
                }
                .onFailure { e ->
                    error = e.message ?: "Registration failed"
                    onError(error!!)
                }

            isLoading = false
        }
    }
}