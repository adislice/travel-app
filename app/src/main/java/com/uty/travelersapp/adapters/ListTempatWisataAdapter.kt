package com.uty.travelersapp.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.uty.travelersapp.DetailTempatWisataActivity
import com.uty.travelersapp.R
import com.uty.travelersapp.models.TempatWisataItem

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

        holder.nama.text = model.nama
        holder.lokasi.text = model.kota + ", " + model.provinsi
//        Glide.with(holder.itemView.context)
//            .load(model.foto?.firstOrNull())
//            .centerCrop()
//            .placeholder(R.drawable.image_placeholder)
//            .error(R.drawable.image_placeholder)
//            .into(holder.gambar)
        holder.gambar.load(model.foto?.firstOrNull()) {
            placeholder(R.drawable.loading_image_placeholder)
            crossfade(true)
        }
    }

    fun updateList(tempatWisataList: List<TempatWisataItem>) {
        this.tempatWisataList.clear()
        this.tempatWisataList.addAll(tempatWisataList)
        notifyDataSetChanged()
    }
}