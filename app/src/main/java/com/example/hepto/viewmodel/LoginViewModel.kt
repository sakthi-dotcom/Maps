package com.example.hepto.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hepto.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = AppDatabase.getDatabase(application).userDao()
    private val sharedPref: SharedPreferences = application.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)

    fun login(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userDao.getUserByEmail(email)
            withContext(Dispatchers.Main) {
                if (user != null && user.password == password) {
                    with(sharedPref.edit()) {
                        putBoolean("isLoggedIn", true)
                        putString("userEmail", user.email)
                        apply()
                    }
                    onSuccess()
                } else if (user == null) {
                    onError("User data not found, Please Sign up")
                } else {
                    onError("Invalid credentials")
                }
            }
        }
    }
}
