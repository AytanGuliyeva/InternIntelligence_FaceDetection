package com.example.internintelligence_facedetection.ui.faceDatabase

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.internintelligence_facedetection.databinding.FragmentFaceDatabaseBinding
import com.example.internintelligence_facedetection.ui.faceDatabase.adapter.UsersAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FaceDatabaseFragment : Fragment() {
    private lateinit var binding: FragmentFaceDatabaseBinding
    val viewModel: FaceDatabaseViewModel by viewModels()
    private lateinit var usersAdapter: UsersAdapter

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
        setupRecyclerView()
        observeViewModel()
        viewModel.fetchUsersFromFirestore()
    }
    private fun setupRecyclerView() {
        usersAdapter = UsersAdapter()
        binding.rvUsers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = usersAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.userList.observe(viewLifecycleOwner) { users ->
            usersAdapter.submitList(users)
        }
    }
    private fun navListener(){
        binding.textDetect.setOnClickListener {
            val action = FaceDatabaseFragmentDirections.actionFaceDatabaseFragmentToCameraFragment()
            findNavController().navigate(action)
        }

    }


}