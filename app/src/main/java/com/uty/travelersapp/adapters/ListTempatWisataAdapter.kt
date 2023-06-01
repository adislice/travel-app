package com.uty.travelersapp.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uty.travelersapp.DetailTempatWisataActivity
import com.uty.travelersapp.R
import com.uty.travelersapp.extensions.Helpers
import com.uty.travelersapp.extensions.Helpers.Companion.makeToast
import com.uty.travelersapp.models.TempatWisataItem
import com.uty.travelersapp.utils.MyLocation

class ListTempatWisataAdapter: RecyclerView.Adapter<ListTempatWisataAdapter.ListTempatWisataViewHolder>() {

    private val tempatWisataList = ArrayList<TempatWisataItem>()

    class ListTempatWisataViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val container = itemView
        val nama = itemView.findViewById<TextView>(R.id.card_tempatwisata_nama)
        val lokasi = itemView.findViewById<TextView>(R.id.card_tempatwisata_lokasi)
        val gambar = itemView.findViewById<ImageView>(R.id.card_tempatwisata_gambar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListTempatWisataViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardview_item_tempatwisata, parent, false)
        return ListTempatWisataViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return tempatWisataList.size
    }

    override fun onBindViewHolder(holder: ListTempatWisataViewHolder, position: Int) {
        val model = tempatWisataList[position]
        holder.container.setOnClickListener {
//            holder.itemView.context.makeToast("item clicked: "+model.id)
            val intent = Intent(holder.itemView.context, DetailTempatWisataActivity::class.java)
            intent.putExtra("DETAIL_TEMPATWISATA", model)
            holder.itemView.context.startActivity(intent)
        }
        var jarak = "5 km"
//        if (MyLocation.location != null) {
//            val jarakk =
//                Helpers.calculateDistance(
//                    MyLocation.location!!.latitude,
//                    MyLocation.location!!.longitude,
//                    model.latitude!!.toDouble(),
//                    model.longitude!!.toDouble()
//                ).toString()
//            jarakk?.let{
//                jarak = it + " KM"
//            }
//        }
        holder.nama.text = model.nama
        holder.lokasi.text = model.alamat
        Glide.with(holder.itemView.context)
            .load(model.thumbnail_foto)
            .centerCrop()
            .placeholder(R.drawable.image_placeholder)
            .error(R.drawable.image_placeholder)
            .into(holder.gambar)
    }

    fun updateList(tempatWisataList: List<TempatWisataItem>) {
        this.tempatWisataList.clear()
        this.tempatWisataList.addAll(tempatWisataList)
        notifyDataSetChanged()
    }
}