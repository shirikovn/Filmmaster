package com.example.mvvmprototyping.screens.shoots

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mvvmprototyping.R
import com.example.mvvmprototyping.databinding.ManageShootsItemBinding
import com.example.mvvmprototyping.itemTouchHelper.shots.ItemTouchHelperAdapterForShots
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.*
import kotlin.collections.HashMap

class ShootsAdapter(val film:String, val scene:String, val fragment: ManageShootsFragment, val shots: MutableList<DocumentSnapshot>):RecyclerView.Adapter<ShootsAdapter.ShootViewHolder>(),
    View.OnClickListener, ItemTouchHelperAdapterForShots {

    class ShootViewHolder(
        val binding: ManageShootsItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShootViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ManageShootsItemBinding.inflate(inflater, parent, false)
        return ShootViewHolder(binding)
    }

    override fun onBindViewHolder(holder:ShootViewHolder, position: Int) {
        holder.binding.shootImage.setOnClickListener(this)
        holder.binding.shootsOptions.setOnClickListener(this)
        holder.binding.shootMoreImageView.setOnClickListener(this)

                val document = shots[position]

                holder.binding.shootImage.tag = document.id
                holder.binding.shootsOptions.tag = document.id

                holder.binding.shootMoreImageView.tag =
                    hashMapOf(
                        "h" to holder,
                        "d" to document.id
                    )

                holder.binding.shotNumber.text = "shoot 0${shots[position].get("N").toString()}"

                holder.binding.cameraShot.adapter = ArrayAdapter.createFromResource(fragment.requireContext(), R.array.camera_framing, android.R.layout.simple_spinner_dropdown_item)
                holder.binding.frameType.adapter = ArrayAdapter.createFromResource(fragment.requireContext(), R.array.camera_shots, android.R.layout.simple_spinner_dropdown_item)
                holder.binding.frameSize.adapter = ArrayAdapter.createFromResource(fragment.requireContext(), R.array.shots_size, android.R.layout.simple_spinner_dropdown_item)

                if (document.contains("cameraShot")){
                   // Log.d("AAAA", document["cameraShot"].toString().toInt().toString())
                    holder.binding.cameraShot.setSelection(document["cameraShot"].toString().toInt())
                }

                if (document.contains("frameType")){
                    holder.binding.frameType.setSelection(document["frameType"].toString().toInt())
                }

                if (document.contains("frameSize")){
                    holder.binding.frameSize.setSelection(document["frameSize"].toString().toInt())
                }

                holder.binding.confirmSpecialOp.setOnClickListener(this)
                holder.binding.confirmSpecialOp.tag =
                    hashMapOf(
                        "cameraShot" to holder.binding.cameraShot,
                        "frameType" to holder.binding.frameType,
                        "frameSize" to holder.binding.frameSize,
                        "doc" to document.id
                    )


                if(document.data!!.keys.contains("PhotoUrl")) {
                    Firebase.storage.reference.child(
                        document.data!!.getValue("PhotoUrl").toString().replaceFirst("/", "")
                    ).downloadUrl.addOnCompleteListener { result1 ->
                        Glide.with(holder.binding.root).load(result1.result.toString()).centerCrop()
                            .into(holder.binding.shootImage)
                    }
                }

    }

    override fun getItemCount(): Int {
        return shots.size
    }

    override fun onClick(p0: View?) {
        if (p0!!.id == R.id.shootImage) {
            fragment.SelectImage(p0!!.tag.toString())
        } else if (p0.id == R.id.shootMoreImageView){
            val v = ((p0.tag as HashMap<*, *>)["h"] as ShootViewHolder).binding.moreOptionsLayout.visibility
            if (v == View.GONE) {
                ((p0.tag as HashMap<*, *>)["h"] as ShootViewHolder).binding.moreOptionsLayout.visibility = View.VISIBLE
                ((p0.tag as HashMap<*, *>)["h"] as ShootViewHolder).binding.shootMoreImageView.rotation = 180F
            } else {
                ((p0.tag as HashMap<*, *>)["h"] as ShootViewHolder).binding.moreOptionsLayout.visibility = View.GONE
                ((p0.tag as HashMap<*, *>)["h"] as ShootViewHolder).binding.shootMoreImageView.rotation = 0F
            }
        }
        else if (p0!!.id == R.id.shootsOptions) {
            Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).document(film)
                .collection("scenes").document(scene).collection("shoots")
                .document(p0.tag.toString()).delete()
                .addOnCompleteListener {
                    fragment.saveReorderdScenes()
                    fragment.reorderScenes()
                    notifyDataSetChanged()
                }
        } else if(p0.id == R.id.confirmSpecialOp){
            val cameraShot = (p0.tag as HashMap<*, *>)["cameraShot"] as Spinner
            val frameType = (p0.tag as HashMap<*, *>)["frameType"] as Spinner
            val frameSize = (p0.tag as HashMap<*, *>)["frameSize"] as Spinner
            val doc = (p0.tag as HashMap<*, *>)["doc"] as String

            Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).document(film)
                .collection("scenes").document(scene).collection("shoots")
                .document(doc).set(
                    hashMapOf(
                        "cameraShot" to cameraShot.selectedItemId,
                        "frameType" to frameType.selectedItemId,
                        "frameSize" to frameSize.selectedItemId
                    ), SetOptions.merge()
                )
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(shots, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(shots, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onItemDismiss(position: Int) {
        return
    }
}