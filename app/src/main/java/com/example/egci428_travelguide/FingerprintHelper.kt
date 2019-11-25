package com.example.egci428_travelguide

import android.Manifest
import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.CancellationSignal
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import com.example.egci428_travelguide.Activity.MapActivity
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey

class FingerprintHelper(private val appContext: Context) : FingerprintManager.AuthenticationCallback() {
    lateinit var cancellationSignal: CancellationSignal

    fun startAuth(manager: FingerprintManager,
                  cryptoObject: FingerprintManager.CryptoObject) {

        cancellationSignal = CancellationSignal()

        if (ActivityCompat.checkSelfPermission(appContext,
                Manifest.permission.USE_FINGERPRINT) !=
            PackageManager.PERMISSION_GRANTED) {
            return
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null)
    }

    override fun onAuthenticationError(errMsgId: Int,
                                       errString: CharSequence) {
        Toast.makeText(appContext,
            "Authentication error\n" + errString,
            Toast.LENGTH_LONG).show()
//        val activity = MapActivity::finishActivity
        val activity = appContext as Activity
        activity.finish()
    }

    override fun onAuthenticationHelp(helpMsgId: Int,
                                      helpString: CharSequence) {
        Toast.makeText(appContext,
            "Authentication help\n" + helpString,
            Toast.LENGTH_LONG).show()
    }

    override fun onAuthenticationFailed() {
        Toast.makeText(appContext,
            "Authentication failed.",
            Toast.LENGTH_LONG).show()
    }

    override fun onAuthenticationSucceeded(
        result: FingerprintManager.AuthenticationResult) {

        Toast.makeText(appContext, "Authentication succeeded.", Toast.LENGTH_LONG).show()

    }
}