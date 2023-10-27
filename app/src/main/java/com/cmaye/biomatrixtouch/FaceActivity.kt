package com.cmaye.biomatrixtouch

import android.os.Build
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import com.cmaye.biomatrixtouch.databinding.ActivityMainBinding
import java.io.IOException
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.Key
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.UnrecoverableKeyException
import java.security.cert.CertificateException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey


/**
 * Created by: rajoo.kannaujiya on 02/16/2020
 */

class FaceActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    @RequiresApi(api = Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.authenticateButton.setOnClickListener(View.OnClickListener { view: View? -> onTouchIdClick() })
        displayBiometricButton()
    }

    private fun onTouchIdClick() {
        biometricPromptHandler.authenticate(
            biometricPrompt, BiometricPrompt.CryptoObject(
                cipher!!
            )
        )
        // Please see the below mentioned note section.
        // getBiometricPromptHandler().authenticate(getBiometricPrompt());
    }

    private val isBiometricCompatibleDevice: Boolean
        private get() {
            return biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS
        }

    private fun displayBiometricButton() {

        //TODO
        Log.e("CMA_Test",biometricManager.canAuthenticate().toString())
        Log.e("CMA_Test",BiometricManager.BIOMETRIC_SUCCESS.toString())
        Log.e("CMA_Test",isBiometricCompatibleDevice.toString())

        if (isBiometricCompatibleDevice) {
            binding.authenticateButton!!.isEnabled = false
        } else {
            binding.authenticateButton!!.isEnabled = true
            generateSecretKey()
        }
    }

    private val biometricManager: BiometricManager
        private get() = BiometricManager.from(this)

    private fun generateSecretKey() {
        var keyGenerator: KeyGenerator? = null
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            KEY_NAME, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .setUserAuthenticationRequired(true)
            .setInvalidatedByBiometricEnrollment(false)
            .build()
        try {
            keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE
            )
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchProviderException) {
            e.printStackTrace()
        }
        if (keyGenerator != null) {
            try {
                keyGenerator.init(keyGenParameterSpec)
            } catch (e: InvalidAlgorithmParameterException) {
                e.printStackTrace()
            }
            keyGenerator.generateKey()
        }
    }

    private val secretKey: SecretKey?
        private get() {
            var keyStore: KeyStore? = null
            var secretKey: Key? = null
            try {
                keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
            } catch (e: KeyStoreException) {
                e.printStackTrace()
            }
            if (keyStore != null) {
                try {
                    keyStore.load(null)
                } catch (e: CertificateException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: NoSuchAlgorithmException) {
                    e.printStackTrace()
                }
                try {
                    secretKey = keyStore.getKey(KEY_NAME, null)
                } catch (e: KeyStoreException) {
                    e.printStackTrace()
                } catch (e: NoSuchAlgorithmException) {
                    e.printStackTrace()
                } catch (e: UnrecoverableKeyException) {
                    e.printStackTrace()
                }
            }
            return secretKey as SecretKey?
        }
    private val cipher: Cipher?
        private get() {
            var cipher: Cipher? = null
            try {
                cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + FORWARD_SLASH
                            + KeyProperties.BLOCK_MODE_CBC + FORWARD_SLASH
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7
                )
                try {
                    cipher.init(Cipher.ENCRYPT_MODE, secretKey)
                } catch (e: InvalidKeyException) {
                    e.printStackTrace()
                }
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            } catch (e: NoSuchPaddingException) {
                e.printStackTrace()
            }
            return cipher
        }
    private val biometricPrompt: PromptInfo
        private get() = PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Login with your biometric credential")
            .setNegativeButtonText("cancel")
            .setConfirmationRequired(false)
            .build()

    private fun onBiometricSuccess() {
        //Call the respective API on biometric success
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
    }

    private val biometricPromptHandler: BiometricPrompt
        private get() = BiometricPrompt(this, ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onBiometricSuccess()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                }
            }
        )

    companion object {
        private val KEY_NAME = "KeyName"
        private val ANDROID_KEY_STORE = "AndroidKeyStore"
        private val FORWARD_SLASH = "/"
    }
}