//package com.cmaye.biomatrixtouch.helper
//
//import android.content.Context
//import android.content.DialogInterface
//import android.hardware.biometrics.BiometricPrompt
//import android.os.Build
//import androidx.annotation.RequiresApi
//
//class BiometricHelper {
//    @RequiresApi(Build.VERSION_CODES.P)
//    fun authenticate(context : Context, callback: BiometricPrompt.AuthenticationCallback) {
//        val biometricPrompt = BiometricPrompt.Builder(context)
//            .setTitle("Fingerprint Authentication")
//            .setSubtitle("Place your finger on the sensor")
//            .setDescription("Touch the sensor to authenticate")
//            .setNegativeButton("Cancel", context.mainExecutor, DialogInterface.OnClickListener { dialog, which ->
//                // Handle cancellation
//            })
//            .build()
//
//        biometricPrompt.authenticate(
//            BiometricPrompt.PromptInfo.Builder()
//                .setConfirmationRequired(false)
//                .build()
//        )
//    }
//}