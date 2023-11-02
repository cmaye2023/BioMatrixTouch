package com.cmaye.biomatrixtouch

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.cmaye.biomatrixtouch.base_interface.signup_interface
import com.cmaye.biomatrixtouch.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity(){
    private lateinit var binding: ActivityLoginBinding
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                displayMessage("Biometric authentication is available")
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                displayMessage("This device doesn't support biometric authentication")
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                displayMessage("Biometric authentication is currently unavailable")
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                displayMessage("No biometric credentials are enrolled")
                // Prompt the user to enroll a biometric credential
                val enrollIntent = Intent()
                enrollIntent.action = android.provider.Settings.ACTION_BIOMETRIC_ENROLL
                enrollIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(enrollIntent)
            }
        }

        val executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                displayMessage("Authentication error: $errString")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                displayMessage("Authentication succeeded!")
                val intent = Intent(this@LoginActivity,MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                displayMessage("Authentication failed")
            }
        })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .setConfirmationRequired(false)
            .build()

        binding.imgFingerPrint.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }

        binding.txtSignUp.setOnClickListener {
            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun displayMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}