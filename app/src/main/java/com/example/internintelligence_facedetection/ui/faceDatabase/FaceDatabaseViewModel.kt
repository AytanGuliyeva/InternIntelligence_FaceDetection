package com.example.internintelligence_facedetection.ui.faceDatabase


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.internintelligence_facedetection.data.Users
import com.google.firebase.firestore.FirebaseFirestore

class FaceDatabaseViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val _userList = MutableLiveData<List<Users>>()
    val userList: LiveData<List<Users>> get() = _userList

    fun fetchUsersFromFirestore() {
        firestore.collection("face_detection_results")
            .get()
            .addOnSuccessListener { documents ->
                val users = mutableListOf<Users>()
                for (document in documents) {
                    val username = document.getString("username") ?: "No Name"
                    val boundingBox = document.getString("boundingBox") ?: "No Data"
                    val imageBase64 = document.getString("image") ?: ""

                    Log.d("Firestore", "Retrieved user: username=$username, boundingBox=$boundingBox, image size=${imageBase64.length}")

                    val user = Users(username, boundingBox, imageBase64)
                    users.add(user)
                }
                _userList.postValue(users)
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Failed to fetch data: ${exception.message}")
            }
    }

}
