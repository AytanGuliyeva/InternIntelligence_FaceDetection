package com.example.internintelligence_facedetection.ui.camera

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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.internintelligence_facedetection.databinding.FragmentCameraBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {
    private lateinit var binding: FragmentCameraBinding
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var previewView: PreviewView
    private lateinit var cameraViewModel: CameraViewModel

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

        cameraViewModel = ViewModelProvider(this).get(CameraViewModel::class.java)

        cameraViewModel.detectedFaceCount.observe(viewLifecycleOwner) { faceCount ->
            binding.tvDetectedFace.text = faceCount
        }

        cameraViewModel.faceBoundingBox.observe(viewLifecycleOwner) { boundingBox ->
            val action = CameraFragmentDirections.actionCameraFragmentToResultFragment(boundingBox)
            findNavController().navigate(action)
        }

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
                cameraViewModel.analyzeImage(imageProxy)
            }

            cameraProvider.bindToLifecycle(
                viewLifecycleOwner,
                cameraSelector,
                preview,
                imageAnalysis
            )
        }, ContextCompat.getMainExecutor(requireContext()))
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
