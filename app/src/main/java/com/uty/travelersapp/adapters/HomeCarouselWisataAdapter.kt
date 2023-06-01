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
import com.uty.travelersapp.models.TempatWisataItem

class HomeCarouselWisataAdapter(val tempatWisataList: ArrayList<TempatWisataItem>) :
    RecyclerView.Adapter<HomeCarouselWisataAdapter.HomeCarouselViewHolder>() {

//    private val newList: ArrayList<TempatWisataModel> = (arrayListOf(tempatWisataList.last()) + tempatWisataList + arrayListOf(tempatWisataList.first())) as ArrayList<TempatWisataModel>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCarouselViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardview_home_carousel, parent, false)
        return HomeCarouselViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return tempatWisataList.size
    }

    override fun onBindViewHolder(holder: HomeCarouselViewHolder, position: Int) {
        val currentItem = tempatWisataList[position]
        val gambar = currentItem.thumbnail_foto
        holder.nama.text = currentItem.nama
        Glide.with(holder.itemView.context)
            .load(gambar)
            .centerCrop()
            .placeholder(R.drawable.image_placeholder)
            .error(R.drawable.image_placeholder)
            .into(holder.imgView)
        val container = holder.itemView
        container.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailTempatWisataActivity::class.java)
            intent.putExtra("DETAIL_TEMPATWISATA", tempatWisataList[position])
            holder.itemView.context.startActivity(intent)
        }
    }

    class HomeCarouselViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nama: TextView = itemView.findViewById(R.id.tv_carousel_tempat_wisata_nama)
        val imgView: ImageView = itemView.findViewById(R.id.img_carousel_tempat_wisata_cover)
    }

}