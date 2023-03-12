package com.uty.travelersapp.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.uty.travelersapp.DetailTempatWisataActivity
import com.uty.travelersapp.R
import com.uty.travelersapp.extensions.Helpers.Companion.calculateDistance
import com.uty.travelersapp.extensions.Helpers.Companion.makeToast
import com.uty.travelersapp.models.TempatWisataModel
import com.uty.travelersapp.utils.MyLocation

class DaftarTempatWisataAdapter(options: FirebaseRecyclerOptions<TempatWisataModel>) :
    FirebaseRecyclerAdapter<TempatWisataModel, DaftarTempatWisataAdapter.TempatWisataViewHolder>(
        options
    ) {

    class TempatWisataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val container = itemView
        val nama = itemView.findViewById<TextView>(R.id.card_tempatwisata_nama)
        val lokasi = itemView.findViewById<TextView>(R.id.card_tempatwisata_lokasi)
        val gambar = itemView.findViewById<ImageView>(R.id.card_tempatwisata_gambar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TempatWisataViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardview_item_tempatwisata, parent, false)
        return TempatWisataViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: TempatWisataViewHolder,
        position: Int,
        model: TempatWisataModel
    ) {
        val key = getRef(position).key
        holder.container.setOnClickListener {
//            holder.itemView.context.makeToast("item clicked: "+key)
            val intent = Intent(holder.itemView.context, DetailTempatWisataActivity::class.java)
            intent.putExtra("DETAIL_TEMPATWISATA", model)
            holder.itemView.context.startActivity(intent)
        }
        var jarak = "5 km"
//        if (MyLocation.location != null) {
//            jarak =
//                calculateDistance(MyLocation.location!!.latitude, MyLocation.location!!.longitude, model.latitude!!.toDouble(), model.longitude!!.toDouble()).toString()
//        }
        holder.nama.text = model.nama
        holder.lokasi.text = jarak
        Glide.with(holder.itemView.context)
            .load(model.gambar)
            .centerCrop()
            .placeholder(R.drawable.image_placeholder)
            .error(R.drawable.image_placeholder)
            .into(holder.gambar)
    }

}