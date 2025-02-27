package com.example.moviemuse.utils

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor

fun authenticateUser(context: Context, onAuthenticated: () -> Unit, onFailed: () -> Unit) {
    val activity = getFragmentActivity(context)

    if (activity == null) {
        Toast.makeText(context, "Error: Unable to retrieve activity context", Toast.LENGTH_SHORT).show()
        onFailed()
        return
    }

    val biometricManager = BiometricManager.from(context)

    // ✅ Allow both strong and weak biometrics
    val canAuthenticate = biometricManager.canAuthenticate(
        BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL
    )

    when (canAuthenticate) {
        BiometricManager.BIOMETRIC_SUCCESS -> {
            val executor: Executor = ContextCompat.getMainExecutor(activity)
            val biometricPrompt = BiometricPrompt(activity, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        onAuthenticated()
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        Toast.makeText(activity, "Authentication failed. Try again.", Toast.LENGTH_SHORT).show()
                        onFailed()
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        Toast.makeText(activity, "Error: $errString", Toast.LENGTH_SHORT).show()
                        onFailed()
                    }
                })

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setSubtitle("Use your fingerprint, face, or PIN to access your profile")
                .setAllowedAuthenticators(
                    BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL
                )  // ✅ Allow all biometrics and PIN
                .build()

            biometricPrompt.authenticate(promptInfo)
        }

        BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
            Toast.makeText(activity, "No biometric hardware detected on this device.", Toast.LENGTH_LONG).show()
            onFailed()
        }

        BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
            Toast.makeText(activity, "Biometric hardware is currently unavailable.", Toast.LENGTH_LONG).show()
            onFailed()
        }

        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
            Toast.makeText(activity, "No biometrics enrolled. Please set up Face or Fingerprint unlock.", Toast.LENGTH_LONG).show()

            // ✅ Redirect user to settings to enroll biometrics
            val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            activity.startActivity(enrollIntent)

            onFailed()
        }

        else -> {
            Toast.makeText(activity, "Biometric authentication is not available.", Toast.LENGTH_LONG).show()
            onFailed()
        }
    }
}

/**
 * ✅ Helper function to retrieve `FragmentActivity`
 */
fun getFragmentActivity(context: Context): FragmentActivity? {
    var ctx = context
    while (ctx is ContextWrapper) {
        if (ctx is FragmentActivity) return ctx
        ctx = ctx.baseContext
    }
    return null
}
