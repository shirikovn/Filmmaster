package com.example.mvvmprototyping.screens.sceneryMode

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvmprototyping.databinding.SceneryModeItemBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SceneryModeAdapter(
    val projectId: String,
    val scenes: MutableList<DocumentSnapshot>
):RecyclerView.Adapter<SceneryModeAdapter.ViewHolder>() {

    class ViewHolder(
        val binding: SceneryModeItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater= LayoutInflater.from(parent.context)
        val binding = SceneryModeItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding

        binding.sceneNameSceneryMode.text = scenes[position]["Title"].toString()

        if (scenes[position].contains("FullScenery")) {
            binding.scenerySceneryMode.setText(scenes[position]["FullScenery"].toString())
        }

        binding.saveLongSceneryMode.setOnClickListener {
            Firebase.firestore.collection(Firebase.auth.currentUser!!.uid)
                .document(projectId).collection("scenes").document(scenes[position].id).set(
                    hashMapOf(
                        "FullScenery" to binding.scenerySceneryMode.text.toString()
                    ), SetOptions.merge()
                )
        }
    }

    override fun getItemCount(): Int {
        return scenes.size
    }
}