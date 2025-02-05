package com.example.internintelligence_facedetection.ui.result

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.internintelligence_facedetection.databinding.FragmentResultBinding
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultFragment : Fragment() {
    private lateinit var binding: FragmentResultBinding
    private val args: ResultFragmentArgs by navArgs()
    val viewModel: ResultViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navListener()
        val boundingBox = args.boundingBox
        val base64Image = args.imageBase64

        binding.textBoundingBox.text = boundingBox
        val bitmap = decodeBase64ToImage(base64Image)
        binding.image.setImageBitmap(bitmap)

        viewModel.saveResult.observe(viewLifecycleOwner, Observer { isSuccess ->
            if (isSuccess) {
                Toast.makeText(requireContext(), "Data added successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Error adding data", Toast.LENGTH_SHORT).show()
            }
        })

        binding.add.setOnClickListener {
            val username = binding.editName.text.toString().trim()

            if (username.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a username!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.saveDataToFirestore(username, boundingBox, base64Image)
        }
    }

    private fun decodeBase64ToImage(base64String: String): Bitmap {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
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
