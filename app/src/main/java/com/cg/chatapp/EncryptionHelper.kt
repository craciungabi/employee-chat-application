package com.cg.chatapp

import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class EncryptionHelper {
    private val initVector = "encryptionIntVec"

    fun encrypt(value: String,  key: String): String {
        try {
            val iv = IvParameterSpec(initVector.toByteArray(charset("UTF-8")))
            val secretKeySpec = SecretKeySpec(key.toByteArray(charset("UTF-8")), "AES")
            val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv)
            val encrypted = cipher.doFinal(value.toByteArray())
            return Base64.getEncoder().encodeToString(encrypted)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return ""
    }

    fun decrypt(value: String,  key: String): String {
        try {
            val iv = IvParameterSpec(initVector.toByteArray(charset("UTF-8")))
            val secretKeySpec = SecretKeySpec(key.toByteArray(charset("UTF-8")), "AES")
            val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv)
            val original = cipher.doFinal(Base64.getDecoder().decode(value))
            return String(original)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return ""
    }
}

fun String.encrypt(key: String): String = EncryptionHelper().encrypt(this, key)

fun String.decrypt(key: String): String = EncryptionHelper().decrypt(this, key)