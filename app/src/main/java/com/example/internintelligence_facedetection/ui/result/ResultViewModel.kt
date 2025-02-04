package com.example.internintelligence_facedetection.ui.result

import android.util.Base64
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class ResultViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _saveResult = MutableLiveData<Boolean>()
    val saveResult: LiveData<Boolean> get() = _saveResult

    fun saveDataToFirestore(username: String, boundingBox: String, base64Image: String) {
        if (username.isEmpty()) {
            _saveResult.postValue(false)
            return
        }

        val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)
        if (decodedBytes.size > 1048487) {
            _saveResult.postValue(false)
            return
        }

        val timestamp = System.currentTimeMillis()
        val userData = hashMapOf(
            "username" to username,
            "boundingBox" to boundingBox,
            "image" to base64Image,
            "timestamp" to timestamp
        )


        firestore.collection("face_detection_results")
            .add(userData)
            .addOnSuccessListener { documentReference ->
                _saveResult.postValue(true)
            }
            .addOnFailureListener { exception ->
                _saveResult.postValue(false)
            }
    }
}
