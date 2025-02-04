package com.example.internintelligence_facedetection.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.internintelligence_facedetection.R
import com.example.internintelligence_facedetection.databinding.FragmentFaceDatabaseBinding
import com.example.internintelligence_facedetection.ui.result.ResultFragmentDirections


class FaceDatabaseFragment : Fragment() {
    private lateinit var binding: FragmentFaceDatabaseBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=  FragmentFaceDatabaseBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navListener()
    }
    private fun navListener(){
        binding.textDetect.setOnClickListener {
            val action = FaceDatabaseFragmentDirections.actionFaceDatabaseFragmentToCameraFragment()
            findNavController().navigate(action)
        }

    }


}