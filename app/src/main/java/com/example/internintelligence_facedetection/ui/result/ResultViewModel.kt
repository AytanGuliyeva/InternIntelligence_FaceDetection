package com.example.internintelligence_facedetection.ui.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class ResultViewModel:ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _saveResult = MutableLiveData<Boolean>()
    val saveResult: LiveData<Boolean> get() = _saveResult

    fun saveDataToFirestore(username: String, boundingBox: String) {
        if (username.isEmpty()) {
            _saveResult.value = false
            return
        }

        val userData = hashMapOf(
            "username" to username,
            "boundingBox" to boundingBox,
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("face_detection_results")
            .add(userData)
            .addOnSuccessListener {
                _saveResult.value = true
            }
            .addOnFailureListener {
                _saveResult.value = false
            }
    }
}