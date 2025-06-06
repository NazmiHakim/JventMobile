package com.example.jvent.viewmodel// com.example.jvent.viewmodel.LoginViewModel.kt
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    // Login form state
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
        error = null // Clear error when user types
    }

    fun updatePassword(newPassword: String) {
        password = newPassword
        error = null // Clear error when user types
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
            try {
                // Simulate network call
                delay(1000)

                // In a real app, you would call your authentication service here
                // val result = authRepository.login(email, password)

                // For demo purposes, we'll just check for a test credential
                if (email == "test@example.com" && password == "password123") {
                    onSuccess()
                } else {
                    onError("Invalid credentials")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Login failed")
            } finally {
                isLoading = false
            }
        }
    }
}