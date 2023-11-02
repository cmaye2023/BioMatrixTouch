package com.cmaye.biomatrixtouch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import android.widget.Toast
import com.cmaye.biomatrixtouch.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                displayMessage("Biometric authentication is available")
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                displayMessage("This device doesn't support biometric authentication")
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                displayMessage("Biometric authentication is currently unavailable")
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                displayMessage("No biometric credentials are enrolled")
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
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                displayMessage("Authentication failed")
            }
        })

//        promptInfo = BiometricPrompt.PromptInfo.Builder()
//            .setTitle("Biometric Authentication")
//            .setSubtitle("Log in using your biometric credential")
//            .setNegativeButtonText("Cancel")
//            .build()

        // Lets the user authenticate without performing an action, such as pressing a
// button, after their biometric credential is accepted.
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .setConfirmationRequired(false)
            .build()


    }

    private fun displayMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
