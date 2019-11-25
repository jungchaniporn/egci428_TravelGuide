package com.example.egci428_travelguide

import android.Manifest
import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey

class FingerprintAuth (private val context: Context, keyguardmng: KeyguardManager, fingerprintmng: FingerprintManager){
    var fingerprintManager: FingerprintManager = fingerprintmng
    var keyguardManager: KeyguardManager = keyguardmng
    val KEY_NAME = "my_key"
    var keyStore:KeyStore? = null
    var keyGenerator:KeyGenerator? = null
    lateinit var cipher: Cipher
    lateinit var cryptoObject: FingerprintManager.CryptoObject
    fun initCipher(): Boolean {
        try {
            cipher = Cipher.getInstance(
                KeyProperties.KEY_ALGORITHM_AES + "/"
                        + KeyProperties.BLOCK_MODE_CBC + "/"
                        + KeyProperties.ENCRYPTION_PADDING_PKCS7)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to get Cipher", e)
        } catch (e: NoSuchPaddingException) {
            throw RuntimeException("Failed to get Cipher", e)
        }

        try {
            keyStore!!.load(null)
            val key = keyStore!!.getKey(KEY_NAME, null) as SecretKey
            cipher.init(Cipher.ENCRYPT_MODE, key)
            return true
        } catch (e: KeyPermanentlyInvalidatedException) {
            return false
        } catch (e: KeyStoreException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: CertificateException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: UnrecoverableKeyException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: IOException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: InvalidKeyException) {
            throw RuntimeException("Failed to init Cipher", e)
        }
    }
    fun generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                "AndroidKeyStore")
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(
                "Failed to get KeyGenerator instance", e)
        } catch (e: NoSuchProviderException) {
            throw RuntimeException("Failed to get KeyGenerator instance", e)
        }

        try {
            keyStore!!.load(null)
            keyGenerator!!.init(
                KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                        KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build())
            keyGenerator!!.generateKey()
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: InvalidAlgorithmParameterException) {
            throw RuntimeException(e)
        } catch (e: CertificateException) {
            throw RuntimeException(e)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

     fun checkLockScreen(): Boolean {
//        keyguardManager = getSystemService(Context.KEYGUARD_SERVICE)
//                as KeyguardManager
//        fingerprintManager = getSystemService(Context.FINGERPRINT_SERVICE)
//                as FingerprintManager
        //some other task
        if (keyguardManager.isKeyguardSecure == false) {

            Toast.makeText(context, "Lock screen security not enabled", Toast.LENGTH_LONG).show()
            return false
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context,
                "Permission not enabled (Fingerprint)",
                Toast.LENGTH_LONG).show()

            return false
        }

        if (fingerprintManager.hasEnrolledFingerprints() == false) {
            Toast.makeText(context,
                "No fingerprint registered, please register",
                Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

}