package com.example.internintelligence_facedetection.ui.camera

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class CameraViewModel : ViewModel() {

    private val _detectedFaceCount = MutableLiveData<String>()
    val detectedFaceCount: LiveData<String> get() = _detectedFaceCount

    private val _faceBoundingBox = MutableLiveData<String>()
    val faceBoundingBox: LiveData<String> get() = _faceBoundingBox

    @OptIn(ExperimentalGetImage::class)
    fun analyzeImage(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            detectFaces(image)
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            Log.d("TAGdetect", "Rotation degrees: $rotationDegrees")
        } else {
            Log.e("TAGdetect", "Image is empty!")
            imageProxy.close()
        }
    }

    private fun detectFaces(image: InputImage) {
        val detector = FaceDetection.getClient(
            FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .build()
        )

        detector.process(image)
            .addOnSuccessListener { faces ->
                if (faces.isNotEmpty()) {
                    _detectedFaceCount.value = "Face Detected: ${faces.size}"
                    val boundingBox = faces[0].boundingBox
                    _faceBoundingBox.value = "$boundingBox"
                    Log.d("TAGdetect", "Faces found: ${faces.size}")
                    faces.forEachIndexed { index, face ->
                        Log.d("TAGdetect", "Face $index: Box = ${face.boundingBox}")
                    }
                } else {
                    _detectedFaceCount.value = "No face detected."
                }
            }
            .addOnFailureListener { e ->
                Log.e("TAGdetect", "Face detection error: ${e.message}")
            }
    }
}