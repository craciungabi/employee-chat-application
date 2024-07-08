package com.cg.chatapp

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await

class FirebaseStorage {
    private val storageRef = Firebase.storage.reference

    suspend fun uploadImage(username: String, path: Uri?) {
        if (path != null) {
            try {
                val fileRef = storageRef.child(username)
                fileRef.putFile(path).await()
            } catch (e: Exception) {
            }
        }
    }

    suspend fun getPath(username: String): String {
        var url = ""
        try {
            storageRef.child(username).downloadUrl.addOnSuccessListener {
                url = it.toString()
            }.await()
        } catch (e: Exception) {
        }

        return url
    }
}