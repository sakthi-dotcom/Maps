package com.example.hepto.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hepto.database.AppDatabase
import com.example.hepto.database.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SignUpViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userDao = AppDatabase.getDatabase(application).userDao()

    fun signUp(
        email: String,
        password: String,
        confirmPassword: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (password == confirmPassword) {
            viewModelScope.launch {
                try {

                    val authResult =
                        firebaseAuth.createUserWithEmailAndPassword(email, password).await()


                    val user = User(email = email, password = password)
                    withContext(Dispatchers.IO) {
                        userDao.insert(user)
                    }

                    onSuccess()
                } catch (e: Exception) {
                    onError(e.message ?: "Sign up failed")
                }
            }
        } else {
            onError("Passwords do not match")
        }
    }
}
