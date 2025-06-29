package com.example.jvent.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jvent.R
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
    var error by mutableStateOf<Int?>(null)
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
            else -> true
        }
    }

    fun login(
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

            authRepository.login(email, password)
                .onSuccess {
                    onSuccess()
                }
                .onFailure { e ->
                    error = R.string.login_failed_error
                    onError(error!!)
                }

            isLoading = false
        }
    }
}