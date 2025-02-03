package com.example.internintelligence_facedetection

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.Manifest
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.internintelligence_facedetection.databinding.FragmentCameraBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {
    private lateinit var binding: FragmentCameraBinding
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var previewView: PreviewView

    companion object {
        const val REQUEST_CODE_CAMERA = 100
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CODE_CAMERA
            )
        } else {
            previewView = binding.previewView
            cameraExecutor = Executors.newSingleThreadExecutor()
            startCamera()
        }
    }

    private fun startCamera() {
        Log.d("TAGdetect", "Kamera başlatılır...")
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            Log.d("TAGdetect", "Kamera Provider əldə edildi")

            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(previewView.surfaceProvider)

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                analyzeImage(imageProxy)
            }

            cameraProvider.bindToLifecycle(
                viewLifecycleOwner,
                cameraSelector,
                preview,
                imageAnalysis
            )
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    @OptIn(ExperimentalGetImage::class)
    private fun analyzeImage(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            detectFaces(image)
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            Log.d("TAGdetect", "Rotation dərəcəsi: $rotationDegrees")
            Log.d("TAGdetect", "Görüntü alındı: ${mediaImage?.format}")
        } else {
            Log.e("TAGdetect", "Görüntü boşdur!")
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
                    binding.tvDetectedFace.text = "Üz Tanındı: ${faces.size}"
                    val face = faces[0]
                    val boundingBox = face.boundingBox
                    val action = CameraFragmentDirections.actionCameraFragmentToResultFragment(
                        //boundingBox.left, boundingBox.top, boundingBox.right, boundingBox.bottom
                    )
                    findNavController().navigate(action)
                } else {
                    binding.tvDetectedFace.text = "Üz tanınmır."
                }
                Log.d("TAGdetect", "Üzlər tapıldı: ${faces.size}")
                faces.forEachIndexed { index, face ->

                    Log.d("TAGdetect", "Üz $index: Box = ${face.boundingBox}")
                }
            }
            .addOnFailureListener { e ->
                Log.e("TAGdetect", "Üz tanıma xətası: ${e.message}")
            }

    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                previewView = binding.previewView
                cameraExecutor = Executors.newSingleThreadExecutor()
                startCamera()
            } else {
                binding.tvDetectedFace.text = "Kamera icazəsi verilməyib."
            }
        }
    }
}
