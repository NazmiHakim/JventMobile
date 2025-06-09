package com.example.jvent.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth: FirebaseAuth = Firebase.auth


    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(
        email: String,
        password: String,
        username: String
    ): Result<FirebaseUser> {
        return try {
            // Create user with email and password
            val result = auth.createUserWithEmailAndPassword(email, password).await()

            // Update user profile with username
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build()

            result.user?.updateProfile(profileUpdates)?.await()

            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        fun logout() {
            Firebase.auth.signOut()
        }
    }

    //suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        //return try {
            //auth.sendPasswordResetEmail(email).await()
            //Result.success(Unit)
        //} catch (e: Exception) {
            //Result.failure(e)
        //}
    //}
}