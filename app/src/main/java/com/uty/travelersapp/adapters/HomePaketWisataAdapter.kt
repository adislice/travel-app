package com.uty.travelersapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import coil.load
import com.uty.travelersapp.PaketWisataBaseActivity
import com.uty.travelersapp.R
import com.uty.travelersapp.models.PaketWisataItem
import com.uty.travelersapp.utils.Helper
import com.uty.travelersapp.utils.IntentKey

class HomePaketWisataAdapter(ctx: Context): BaseAdapter() {
    private var ctx = ctx
    private var paketWisataList = arrayListOf<PaketWisataItem>()
    private lateinit var txtNamaPaket: TextView
    private lateinit var txtDestinasi: TextView
    private lateinit var txtHarga: TextView
    private lateinit var txtWaktu: TextView
    private lateinit var imgThumbnail: ImageView

    override fun getCount(): Int {
        return paketWisataList.size
    }

    override fun getItem(position: Int): Any {
        return paketWisataList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView: View? = convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(ctx)
                .inflate(R.layout.item_home_paketwisata, parent, false)
        }
        convertView?.setOnClickListener {
            val intent = Intent(convertView.context, PaketWisataBaseActivity::class.java)
            intent.putExtra(IntentKey.DETAIL_PAKET_WISATA, paketWisataList[position].id)
            convertView.context.startActivity(intent)
        }
        txtNamaPaket = convertView!!.findViewById(R.id.txt_nama_paketwisata)
        txtDestinasi = convertView.findViewById(R.id.txt_destinasi)
        txtHarga = convertView.findViewById(R.id.txt_harga)
        txtWaktu = convertView.findViewById(R.id.txt_waktu)
        imgThumbnail = convertView.findViewById(R.id.img_thumbnail)

        var harga = ""
        txtHarga.text = "Mulai dari -"
        paketWisataList[position].produk?.let {
            var produkList = paketWisataList[position].produk!!.sortedBy { it.harga }
            val hargaStr = produkList.first().harga?.let { it1 -> Helper.formatRupiah(it1) }
            txtHarga.text = "Mulai dari ${hargaStr}"
        }

        var destinasi = ""
        val listProv = paketWisataList[position].tempat_wisata_data?.map { it.provinsi }
        var listProvNew = listProv?.distinct()
        txtNamaPaket.text = paketWisataList[position].nama
//        txtDestinasi.text = listProvNew?.joinToString()
        txtDestinasi.text = paketWisataList[position].tempat_wisata_data?.size.toString() + " objek wisata tujuan"

        txtWaktu.text = "1 hari"

        imgThumbnail.load(paketWisataList[position].foto?.first()) {
            placeholder(R.drawable.image_placeholder)
            crossfade(true)
        }

        return convertView
    }

    fun updateList(newList: ArrayList<PaketWisataItem>) {
        paketWisataList.clear()
        paketWisataList.addAll(newList)
        notifyDataSetChanged()
    }
}