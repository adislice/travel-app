package com.uty.travelersapp.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.uty.travelersapp.DetailPaketWisataActivity
import com.uty.travelersapp.R
import com.uty.travelersapp.models.BiroWisataModel
import com.uty.travelersapp.models.PaketWisataModel
import com.uty.travelersapp.utils.FirebaseUtils

class DaftarPaketWisataAdapter (val paketWisataList: ArrayList<PaketWisataModel>):
    RecyclerView.Adapter<DaftarPaketWisataAdapter.DaftarPaketWisataViewHolder>() {
    class DaftarPaketWisataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val container = itemView
        val nama = itemView.findViewById<TextView>(R.id.card_paketwisata_nama)
        val birowisata = itemView.findViewById<TextView>(R.id.card_paketwisata_birowisata)
        val gambar = itemView.findViewById<ImageView>(R.id.card_paketwisata_gambar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaftarPaketWisataViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardview_item_paketwisata, parent, false)
        return DaftarPaketWisataViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return paketWisataList.size
    }

    override fun onBindViewHolder(holder: DaftarPaketWisataViewHolder, position: Int) {
        val model = paketWisataList[position]
        holder.container.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailPaketWisataActivity::class.java)
            intent.putExtra("DETAIL_PAKETWISATA", model)
            holder.itemView.context.startActivity(intent)
        }
        holder.nama.text = model.nama
//        model.birowisata?.let {
//            FirebaseUtils.firebaseDatabase.document(it).get()
//                .addOnSuccessListener { doc ->
//                    val biroNama = doc.getString("nama")
//                    holder.birowisata.text = biroNama
//                }
//        }


    }

}