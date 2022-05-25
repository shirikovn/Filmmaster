package com.example.mvvmprototyping.screens.projects.create

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri

import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mvvmprototyping.databinding.FragmentCreateProjectBinding
import com.example.mvvmprototyping.screens.base.BaseFragment
import com.example.mvvmprototyping.screens.base.BaseScreen
import com.example.mvvmprototyping.screens.base.screenViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.IOException
import java.util.*


class CreateProjectFragment(): BaseFragment(

) {

    private val PICK_IMAGE_REQUEST = 22
    var filePath = Uri.EMPTY

        override val viewModel by screenViewModel<CreateProjectViewModel>()
        class Screen(val project: String): BaseScreen

    lateinit var binding: FragmentCreateProjectBinding

    val storageRef = Firebase.storage.reference
    val imagesRef: StorageReference = storageRef.child("images")
    lateinit var imageUrl: StorageReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val flag = (viewModel.project1.value == "")
        binding = FragmentCreateProjectBinding.inflate(inflater, container, false)
        val db = Firebase.firestore

        if (flag) {
            binding.createNewProjectButton.setOnClickListener {
                if (binding.newProjectTeamTI.text != null && binding.newProjectTitleTI.text != null) {
                    if (!Uri.EMPTY.equals(filePath)) {

                        binding.addProjectProgressBar.visibility = View.VISIBLE
                        binding.createNewProjectButton.isClickable = false

                        imageUrl = imagesRef.child(UUID.randomUUID().toString())
                        imageUrl.putFile(filePath).addOnCompleteListener {
                            db.collection(Firebase.auth.currentUser!!.uid).add(
                                    hashMapOf(
                                        "Title" to binding.newProjectTitleTI.text.toString(),
                                        "Team" to binding.newProjectTeamTI.text.toString(),
                                        "PhotoUrl" to imageUrl.path,
                                        "T" to Calendar.getInstance().timeInMillis
                                    ),
                                ).addOnCompleteListener() {
                                    if (it.isSuccessful) viewModel.launchChooseProject()
                                }
                        }
                    }
                }
            }
        } else {
            binding.createProjectTitle.text = "Change project"
            binding.createNewProjectButton.setOnClickListener {
                if (binding.newProjectTeamTI.text != null && binding.newProjectTitleTI.text != null) {
                    if (!Uri.EMPTY.equals(filePath)) {
                        binding.addProjectProgressBar.visibility = View.VISIBLE
                        binding.createNewProjectButton.isClickable = false

                        imageUrl = imagesRef.child(UUID.randomUUID().toString())
                        imageUrl.putFile(filePath).addOnCompleteListener {
                            db.collection(Firebase.auth.currentUser!!.uid)
                                .document(viewModel.project1.value!!).set(
                                    hashMapOf(
                                        "Title" to binding.newProjectTitleTI.text.toString(),
                                        "Team" to binding.newProjectTeamTI.text.toString(),
                                        "PhotoUrl" to imageUrl.path
                                    ),
                                ).addOnCompleteListener() {
                                    if (it.isSuccessful) viewModel.launchChooseProject()
                                }
                        }
                    }
                }
            }
        }

        binding.loadImage.setOnClickListener {
            SelectImage()
        }

        return binding.root

    }

    private fun SelectImage() {

        // Defining Implicit Intent to mobile gallery
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(
                intent,
                "Select Image from here..."
            ),
            PICK_IMAGE_REQUEST
        )
    }

//    private fun uploadImage(){
//        if (filePath != null){
//            imagesRef!!.child(UUID.randomUUID().toString()).putFile(filePath)
//        }
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST
            && resultCode == RESULT_OK
            && data != null
            && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData()!!

            try {
                val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, filePath)
                binding.loadImage.setImageBitmap(bitmap)
            }
            catch (e: IOException){
                e.printStackTrace()
            }
            }
    }
}