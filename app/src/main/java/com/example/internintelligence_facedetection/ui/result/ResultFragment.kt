package com.example.internintelligence_facedetection.ui.result

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.internintelligence_facedetection.ParcelableImageData
import com.example.internintelligence_facedetection.databinding.FragmentResultBinding
import com.example.internintelligence_facedetection.ui.camera.CameraFragmentDirections


class ResultFragment : Fragment() {
    private lateinit var binding: FragmentResultBinding
    val args: ResultFragmentArgs by navArgs()
    private val viewModel: ResultViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentResultBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navListener()
        val boundingBox = args.boundingBox
        binding.textBoundingBox.text = boundingBox

        binding.add.setOnClickListener {
            val username = binding.editName.text.toString().trim()
            val boundingBoxText = binding.textBoundingBox.text.toString()
            viewModel.saveDataToFirestore(username, boundingBoxText)
        }

        viewModel.saveResult.observe(viewLifecycleOwner, Observer { isSuccess ->
            if (isSuccess) {
                Toast.makeText(requireContext(), "Data added successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Error adding data", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun navListener(){
        binding.textDetect.setOnClickListener {
            val action = ResultFragmentDirections.actionResultFragmentToCameraFragment()
            findNavController().navigate(action)
        }
        binding.look.setOnClickListener {
            val action = ResultFragmentDirections.actionResultFragmentToFaceDatabaseFragment()
            findNavController().navigate(action)
        }
    }



}