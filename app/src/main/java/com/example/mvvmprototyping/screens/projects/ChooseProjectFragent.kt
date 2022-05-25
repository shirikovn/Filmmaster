package com.example.mvvmprototyping.screens.projects

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvvmprototyping.App
import com.example.mvvmprototyping.MainActivity
import com.example.mvvmprototyping.R
import com.example.mvvmprototyping.databinding.FragmentChooseProjecctBinding
import com.example.mvvmprototyping.screens.base.BaseFragment
import com.example.mvvmprototyping.screens.base.BaseScreen
import com.example.mvvmprototyping.screens.base.BaseViewModel
import com.example.mvvmprototyping.screens.base.screenViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.parcelize.Parcelize

class ChooseProjectFragment(
) : BaseFragment() {

    override val viewModel by screenViewModel<ChooseProjectViewModel>()

    class Screen(): BaseScreen

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentChooseProjecctBinding.inflate(inflater, container, false)

        val adapter = ProjectAdapter(viewModel)
        val layoutManager = LinearLayoutManager(requireContext())

        binding.recyclerViewChooseProject.adapter = adapter
        binding.recyclerViewChooseProject.layoutManager = layoutManager

        binding.loadProjectsProgressBar.visibility = View.VISIBLE

        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).get()
            .addOnSuccessListener { result ->
                adapter.itemsCount2 = result.documents.size
                binding.loadProjectsProgressBar.visibility = View.GONE
            }

        binding.topAppBar.setOnMenuItemClickListener {
            if (it.itemId == R.id.account) {
                Firebase.auth.signOut()
                viewModel.launchAuth()
                return@setOnMenuItemClickListener true
            }
            false
        }

        return binding.root
    }
}