package com.example.cs360projecttwo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.example.cs360projecttwo.database.UserDatabaseHelper
import com.example.cs360projecttwo.ui.theme.LoginScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {
    private lateinit var userDbHelper: UserDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userDbHelper = UserDatabaseHelper(this)

        setContent {
            LoginScreen(
                onLogin = { username, password ->
                    lifecycleScope.launch(Dispatchers.Main) {
                        val isValidUser = userDbHelper.loginUser(username, password)
                        if (isValidUser) {
                            navigateToMainActivity()
                        } else {
                            Toast.makeText(this@LoginActivity, "Invalid username or password!", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                onRegister = { username, password ->
                    lifecycleScope.launch(Dispatchers.Main) {
                        val isRegistered = userDbHelper.registerUser(username, password)
                        if (isRegistered) {
                            Toast.makeText(this@LoginActivity, "Registration Successful!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@LoginActivity, "User already exists!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
