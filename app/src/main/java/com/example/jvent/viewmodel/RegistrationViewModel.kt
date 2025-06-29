package com.example.jvent.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jvent.R
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
    var error by mutableStateOf<Int?>(null)
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
                error = R.string.username_empty_error
                false
            }
            email.isBlank() -> {
                error = R.string.email_empty_error
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                error = R.string.invalid_email_error
                false
            }
            password.isBlank() -> {
                error = R.string.password_empty_error
                false
            }
            password.length < 6 -> {
                error = R.string.password_length_error
                false
            }
            confirmPassword != password -> {
                error = R.string.passwords_no_match_error
                false
            }
            else -> true
        }
    }

    fun register(
        onSuccess: () -> Unit,
        onError: (Int) -> Unit
    ) {
        if (!validateForm()) {
            onError(error ?: R.string.invalid_form_error)
            return
        }

        viewModelScope.launch {
            isLoading = true
            error = null

            authRepository.register(email, password, username)
                .onSuccess {
                    onSuccess()
                }
                .onFailure {
                    error = R.string.registration_failed_error
                    onError(error!!)
                }

            isLoading = false
        }
    }
}