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
import com.uty.travelersapp.models.TempatWisataArrayItem
import com.uty.travelersapp.models.TempatWisataItem

class ListTujuanWisataAdapter(): RecyclerView.Adapter<ListTujuanWisataAdapter.ListTujuanWisataViewHolder>()  {
    private val tujuanWisataList = ArrayList<TempatWisataArrayItem>()
    class ListTujuanWisataViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val container = itemView
        val nama = itemView.findViewById<TextView>(R.id.list_tujuan_nama_tempat_wisata)
        val order = itemView.findViewById<TextView>(R.id.list_tujuan_order)
        val gambar = itemView.findViewById<ImageView>(R.id.list_tujuan_gambar)
        val alamat = itemView.findViewById<TextView>(R.id.list_tujuan_alamat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListTujuanWisataViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.tujuan_wisata_item, parent, false)
        return ListTujuanWisataViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return tujuanWisataList.size
    }

    override fun onBindViewHolder(holder: ListTujuanWisataViewHolder, position: Int) {
        val data = tujuanWisataList[position]

        holder.nama.text = data.tempat_wisata_data?.nama
        holder.order.text = "TUJUAN " + data.order.toString()
        holder.alamat.text = data.tempat_wisata_data?.alamat
        Glide.with(holder.itemView.context)
            .load(data.tempat_wisata_data?.foto?.firstOrNull())
            .centerCrop()
            .placeholder(R.drawable.image_placeholder)
            .error(R.drawable.image_placeholder)
            .into(holder.gambar)

        holder.container.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailTempatWisataActivity::class.java)
            intent.putExtra("DETAIL_TEMPATWISATA", TempatWisataItem(data.tempat_wisata_id, data.tempat_wisata_data?.nama))
            holder.itemView.context.startActivity(intent)
        }
    }
    fun updateList(tujuanWisataList: List<TempatWisataArrayItem>) {
        this.tujuanWisataList.clear()
        this.tujuanWisataList.addAll(tujuanWisataList)
        notifyDataSetChanged()
    }

}