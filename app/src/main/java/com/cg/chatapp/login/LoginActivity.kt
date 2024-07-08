package com.cg.chatapp.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cg.chatapp.SharedPrefsRepository
import com.cg.chatapp.databinding.ActivityLoginBinding
import com.cg.chatapp.main.MainActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val prefs: SharedPrefsRepository by lazy {
        SharedPrefsRepository(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (prefs.getUser().isNotEmpty()) {
            startMainActivity()
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        this.finish()
    }
}