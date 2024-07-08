package com.cg.chatapp.login

import android.app.Application
import androidx.lifecycle.*
import com.cg.chatapp.FirestoreDatabase
import com.cg.chatapp.SharedPrefsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginViewModel(
    private val context: Application
) : ViewModel() {
    val credentials: UserCredentials = UserCredentials()
    val loginClicked = MutableStateFlow(false)
    val credentialsError = MutableStateFlow("")

    private val database = FirestoreDatabase()

    private val prefs: SharedPrefsRepository by lazy {
        SharedPrefsRepository(context)
    }

    init {
        collectCredentialsState()
    }

    fun onLogin() {
        viewModelScope.launch {
            if (checkCredentials()) {
                val user = database.getUser(credentials.username.value)

                if (credentials.password.value == user.password) {
                    prefs.loginUser(
                        credentials.username.value,
                        user.access
                    )

                    loginClicked.value = true
                } else {
                    credentialsError.value = INVALID_CREDENTIALS
                }
            } else {
                credentialsError.value = INVALID_CREDENTIALS
            }
        }
    }

    private fun checkCredentials() =
        credentials.username.value.length > 2 && credentials.password.value.length > 2


    private fun isCredentialsError() = credentialsError.value != ""

    private fun resetCredentialsError() {
        credentialsError.value = ""
    }

    private fun collectCredentialsState() {
        viewModelScope.launch {
            credentials.username.collectLatest {
                if (isCredentialsError()) resetCredentialsError()
            }
        }
        viewModelScope.launch {
            credentials.password.collectLatest {
                if (isCredentialsError()) resetCredentialsError()
            }
        }
    }

    open class UserCredentials {
        val username: MutableStateFlow<String> = MutableStateFlow("")
        val password: MutableStateFlow<String> = MutableStateFlow("")
    }

    companion object {
        private const val INVALID_CREDENTIALS = "Invalid Credentials"
    }
}