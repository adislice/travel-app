package com.uty.travelersapp.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Pemesanan(
    var id: String? = null,
    val kode_pemesanan: String? = "",
    val user_id: String? = null,
    var user_nama: String? = null,
    var user_no_telp: String? = null,
    val produk_id: String? = null,
    var produk_harga: Double? = 0.0,
    var jenis_kendaraan_id: String? = null,
    var jenis_kendaraan_nama: String? = null,
    var jenis_kendaraan_jumlah_seat: Int? = 0,
    val paket_wisata_id: String? = null,
    var paket_wisata_nama: String? = null,
    var paket_wisata_foto: String? = null,
    val promo_id: String? = null,
    var promo_kode: String? = null,
    var promo_nama: String? = null,
    var promo_persen: Int? = null,
    var promo_potongan: Double = 0.0,
    val lokasi_jemput_lng: String? = null,
    val lokasi_jemput_lat: String? = null,
    val tanggal_keberangkatan: Date? = null,
    val jam_keberangkatan: String? = null,
    val metode_bayar: String? = null,
    val batas_bayar: Date? = null,
    val tanggal_bayar: Date? = null,
    val status: Status? = Status.DIPROSES,
    val total_bayar: Double? = 0.0,
    val payment_url: String? = null,
    @ServerTimestamp val created_at: Date? = null,
)

data class PemesananInsert(
    var id: String? = null,
    val kode_pemesanan: String? = "",
    val user_id: String? = null,
    var user_nama: String? = null,
    var user_no_telp: String? = null,
    val produk_id: String? = null,
    var produk_harga: Double? = 0.0,
    val paket_wisata_id: String? = null,
    val promo_id: String? = null,
    var promo_potongan: Double = 0.0,
    val lokasi_jemput_lng: String? = null,
    val lokasi_jemput_lat: String? = null,
    val tanggal_keberangkatan: Date? = null,
    val jam_keberangkatan: String? = null,
    val metode_bayar: String? = null,
    val batas_bayar: Date? = null,
    val tanggal_bayar: Date? = null,
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