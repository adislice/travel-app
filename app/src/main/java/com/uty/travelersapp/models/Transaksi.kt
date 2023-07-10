package com.uty.travelersapp.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Transaksi(
    @DocumentId var id: String? = null,
    val kode_transaksi: String? = "",
    val user: TransaksiUser? = null,
    val tanggal: Date? = null,
    val total_bayar: Double? = 0.0,
    val status: Status? = Status.BELUM_BAYAR,
    val batas_pembayaran: Date? = null,
    val payment_url: String? = null,
    val paket_wisata: TransaksiPaketWisata? = null,
    val produk: TransaksiProduk? = null,
    val promo: TransaksiPromo? = null,
    val keberangkatan: TransaksiKeberangkatan? = null,
    @ServerTimestamp val created_at: Date? = null,
)

enum class Status {
    BELUM_BAYAR,
    PENDING,
    SELESAI
}

data class TransaksiKeberangkatan(
    val tanggal_perjalanan: Date? = null,
    val lokasi_penjemputan_lat: String? = null,
    val lokasi_penjemputan_long: String? = null
)

data class TransaksiLokasiPenjemputan(
    val latitude: String? = null,
    val longitude: String? = null,
    val catatan: String? = null
)

data class TransaksiUser(
    val id: String? = null,
    val nama: String? = null,
    val no_telp: String? = null
)