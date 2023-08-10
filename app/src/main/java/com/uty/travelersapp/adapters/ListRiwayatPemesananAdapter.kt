package com.uty.travelersapp.adapters

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.uty.travelersapp.PaketWisataBaseActivity
import com.uty.travelersapp.R
import com.uty.travelersapp.models.Status
import com.uty.travelersapp.models.Pemesanan
import com.uty.travelersapp.utils.ColorStatus
import com.uty.travelersapp.utils.Helper
import com.uty.travelersapp.utils.IntentKey
import java.text.SimpleDateFormat
import java.util.Locale

class ListRiwayatPemesananAdapter: RecyclerView.Adapter<ListRiwayatPemesananAdapter.ListRiwayatPemesananViewHolder>() {
    private val riwayatPemesananList = ArrayList<Pemesanan>()
    class ListRiwayatPemesananViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val container = itemView
        val namaPaket = itemView.findViewById<TextView>(R.id.txt_nama_paketwisata)
        val kodeTransaksi = itemView.findViewById<TextView>(R.id.txt_kode_transaksi)
        val gambar = itemView.findViewById<ImageView>(R.id.img_paketwisata)
        val totalBayar = itemView.findViewById<TextView>(R.id.txt_total_bayar)
        val status = itemView.findViewById<TextView>(R.id.txt_status)
        val tanggalPerjalanan = itemView.findViewById<TextView>(R.id.txt_tanggal_perjalanan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListRiwayatPemesananViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_riwayat_pemesanan, parent, false)
        return ListRiwayatPemesananViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return riwayatPemesananList.size
    }

    override fun onBindViewHolder(holder: ListRiwayatPemesananViewHolder, position: Int) {
        val model = riwayatPemesananList[position]
//        holder.container.setOnClickListener {
//            val intent = Intent(holder.itemView.context, RiwayatPemesananBaseActivity::class.java)
//            intent.putExtra("DETAIL_RiwayatPemesanan", model)
//            holder.itemView.context.startActivity(intent)
//        }

        holder.container.setOnClickListener {
            val intent = Intent(holder.itemView.context, PaketWisataBaseActivity::class.java)
            intent.putExtra(IntentKey.TRANSAKSI_ID, model.id)
            holder.itemView.context.startActivity(intent)
        }
        val format = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))
        holder.namaPaket.text = "${model.paket_wisata_nama} (${model.jenis_kendaraan_nama})"

        holder.status.text = when(model.status) {
            Status.DIPROSES -> "Diproses"
            Status.SELESAI -> "Selesai"
            Status.PENDING -> "Pending"
            else -> ""
        }
        when(model.status) {
            Status.DIPROSES -> {
                holder.status.text = "DIPROSES"
                holder.status.setTextColor(ColorStatus.BELUM_BAYAR_TEXT)
                holder.status.backgroundTintList = ColorStateList.valueOf(ColorStatus.BELUM_BAYAR_BG)
            }
            Status.SELESAI -> {
                holder.status.text = "SELESAI"
                holder.status.setTextColor(ColorStatus.SELESAI_TEXT)
                holder.status.backgroundTintList = ColorStateList.valueOf(ColorStatus.SELESAI_BG)
            }
            Status.PENDING -> {
                holder.status.text = "PENDING"
                holder.status.setTextColor(ColorStatus.PENDING_TEXT)
                holder.status.backgroundTintList = ColorStateList.valueOf(ColorStatus.PENDING_BG)
            }
            Status.DIBATALKAN -> {
                holder.status.text = "DIBATALKAN"
                holder.status.setTextColor(ColorStatus.DIBATALKAN_TEXT)
                holder.status.backgroundTintList = ColorStateList.valueOf(ColorStatus.DIBATALKAN_BG)
            }
            else -> {
                holder.status.text = "TIDAK DIKETAHUI"
                holder.status.setTextColor(Color.GRAY)
                holder.status.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
            }
        }
//        holder.tanggalPerjalanan.text = model.keberangkatan?.tanggal_perjalanan?.let { format.format(it) }
        holder.kodeTransaksi.text = model.kode_pemesanan
        holder.tanggalPerjalanan.text = "Keberangkatan: ${Helper.dateToTanggalLengkap(model.tanggal_keberangkatan)}"
        holder.totalBayar.text = model.total_bayar?.let { Helper.formatRupiah(it) }
        holder.gambar.load(model.paket_wisata_foto) {
            placeholder(R.drawable.loading_image_placeholder)
            crossfade(true)
        }
    }

    fun updateList(newRiwayatPemesananList: ArrayList<Pemesanan>) {
        this.riwayatPemesananList.clear()
        this.riwayatPemesananList.addAll(newRiwayatPemesananList)
        notifyDataSetChanged()
    }

}