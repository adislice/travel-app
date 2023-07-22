package com.uty.travelersapp.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Pemesanan(
    @DocumentId var id: String? = null,
    val kode_pemesanan: String? = "",
    val user: PemesananUser? = null,
    val produk: PemesananProduk? = null,
    val jenis_kendaraan: PemesananJenisKendaraan? = null,
    val paket_wisata: PemesananPaketWisata? = null,
    val promo: PemesananPromo? = null,
    val keberangkatan: PemesananKeberangkatan? = null,
    val pembayaran: PemesananPembayaran? = null,
    val status: Status? = Status.DIPROSES,
    val total_bayar: Double? = 0.0,
    val payment_url: String? = null,
    @ServerTimestamp val created_at: Date? = null,
)

enum class Status {
    DIPROSES,
    PENDING,
    SELESAI,
    DIBATALKAN
}

data class PemesananKeberangkatan(
    val tanggal_perjalanan: Date? = null,
    val lokasi_jemput_lat: String? = null,
    val lokasi_jemput_lng: String? = null
)

data class PemesananJenisKendaraan(
    val id: String? = null,
    val nama: String? = null,
    val jumlah_seat: Int? = null
)
data class TransaksiLokasiPenjemputan(
    val latitude: String? = null,
    val longitude: String? = null,
    val catatan: String? = null
)

data class PemesananUser(
    val id: String? = null,
    val nama: String? = null,
    val no_telp: String? = null,
    val email: String? = null
)

data class PemesananPembayaran(
    val metode: String? = null,
    val batas_bayar: Date? = null,
    val tanggal_bayar: Date? = null
)