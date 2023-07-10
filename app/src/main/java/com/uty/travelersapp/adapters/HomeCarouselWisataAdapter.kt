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

class HomeCarouselWisataAdapter(val tempatWisataList: ArrayList<TempatWisataItem>) :
    RecyclerView.Adapter<HomeCarouselWisataAdapter.HomeCarouselViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCarouselViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardview_home_carousel, parent, false)
        return HomeCarouselViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return tempatWisataList.size
//        if (tempatWisataList.size > 0) {
//            return Integer.MAX_VALUE
//        } else {
//            return tempatWisataList.size
//        }

    }

    override fun onBindViewHolder(holder: HomeCarouselViewHolder, position: Int) {
//        val currentItem = tempatWisataList[position % tempatWisataList.size]
        val currentItem = tempatWisataList[position]
        val gambar = currentItem.foto!!.firstOrNull()
        holder.nama.text = currentItem.nama

        holder.imgView.load(gambar) {
            placeholder(R.drawable.loading_image_placeholder)
            crossfade(true)
        }
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

    companion object {
        const val REFRESH_RATE_SECONDS = 8000L
    }

}