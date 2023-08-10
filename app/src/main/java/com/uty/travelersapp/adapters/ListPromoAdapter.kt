package com.uty.travelersapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.uty.travelersapp.R
import com.uty.travelersapp.models.ForecastData
import com.uty.travelersapp.models.Promo
import com.uty.travelersapp.utils.Helper

class ListPromoAdapter(): RecyclerView.Adapter<ListPromoAdapter.ListPromoViewHolder>() {

    var promoList = listOf<Promo>()

    class ListPromoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val container = itemView
        val txtNama = itemView.findViewById<TextView>(R.id.txt_promo_nama)
        val txtKode = itemView.findViewById<TextView>(R.id.txt_promo_kode)
        val txtPeriode = itemView.findViewById<TextView>(R.id.txt_promo_periode)
        val txtDetail = itemView.findViewById<TextView>(R.id.txt_promo_detail)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListPromoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_promo, parent, false)
        return ListPromoViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return promoList.size
    }

    override fun onBindViewHolder(holder: ListPromoViewHolder, position: Int) {
        val data = promoList[position]

        holder.txtNama.text = data.nama
        holder.txtKode.text = "Kode: "+data.kode +" • "+"${Helper.dateToTanggalSingkat(data.tanggal_mulai)} - ${Helper.dateToTanggalSingkat(data.tanggal_akhir)}"
        holder.txtPeriode.visibility = View.GONE
        holder.txtDetail.text = "Diskon ${data.persen}% (maks. ${Helper.formatRupiah(data.max_potongan!!)}) \nMin. pembelian: ${Helper.formatRupiah(data.min_pembelian!!)}"

    }

    fun updateList(newList: List<Promo>) {
        promoList = newList
        notifyDataSetChanged()
    }
}