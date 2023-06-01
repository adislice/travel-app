package com.uty.travelersapp.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uty.travelersapp.DetailPaketWisataActivity
import com.uty.travelersapp.R
import com.uty.travelersapp.models.PaketWisataItem
import com.uty.travelersapp.models.TempatWisataItem

class ListPaketWisataAdapter: RecyclerView.Adapter<ListPaketWisataAdapter.ListPaketWisataViewHolder>() {
    private val paketWisataList = ArrayList<PaketWisataItem>()
    class ListPaketWisataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val container = itemView
        val nama = itemView.findViewById<TextView>(R.id.card_paketwisata_nama)
        val deskripsi = itemView.findViewById<TextView>(R.id.card_paketwisata_desc)
        val gambar = itemView.findViewById<ImageView>(R.id.card_paketwisata_gambar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListPaketWisataViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardview_item_paketwisata, parent, false)
        return ListPaketWisataViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return paketWisataList.size
    }

    override fun onBindViewHolder(holder: ListPaketWisataViewHolder, position: Int) {
        val model = paketWisataList[position]
        holder.container.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailPaketWisataActivity::class.java)
            intent.putExtra("DETAIL_PAKETWISATA", model)
            holder.itemView.context.startActivity(intent)
        }
        holder.nama.text = model.nama
        holder.deskripsi.text = model.deskripsi
        Glide.with(holder.itemView.context)
            .load(model.thumbnail_foto)
            .centerCrop()
            .placeholder(R.drawable.image_placeholder)
            .error(R.drawable.image_placeholder)
            .into(holder.gambar)
    }

    fun updateList(paketWisataList: List<PaketWisataItem>) {
        this.paketWisataList.clear()
        this.paketWisataList.addAll(paketWisataList)
        notifyDataSetChanged()
    }

}