package com.uty.travelersapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.BillingAddress
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.ItemDetails
import com.midtrans.sdk.corekit.models.ShippingAddress
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import com.uty.travelersapp.extensions.Helpers.Companion.makeToast
import com.uty.travelersapp.models.Response
import com.uty.travelersapp.models.Status
import com.uty.travelersapp.utils.Helper
import com.uty.travelersapp.utils.IntentKey
import com.uty.travelersapp.utils.MyUser
import com.uty.travelersapp.viewmodel.PemesananViewModel

class ProsesTransaksiActivity : AppCompatActivity() {
    private var idPemesanan = ""
    private val pemesananViewModel by viewModels<PemesananViewModel>()
    private var isProcessed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proses_transaksi)

        val loadingSpinner = Helper.buildLoadingSpinner(this, "Loading", "Memuat pemesanan...")
        
        val i = intent.getStringExtra(IntentKey.TRANSAKSI_ID)
        if (i != null) {
            idPemesanan = i
        }

        if (idPemesanan.isEmpty()) {
            makeToast("Pemesanan tidak ditemukan")
            finish()
        }

        pemesananViewModel.getDetailPemesanan(idPemesanan).observe(this) { response ->

            when(response) {
                is Response.Loading -> {
                    loadingSpinner.show()
                }
                is Response.Success -> {
                    loadingSpinner.dismiss()
                    val namaProdukPaket = "${response.data?.paket_wisata?.nama} (${response.data?.jenis_kendaraan?.nama})"

                    if (response.data?.payment_url != null && response.data.status == Status.PENDING && !isProcessed) {
                        isProcessed = true
                        val transactionUrl = response.data.payment_url
                        val webView: WebView = findViewById(R.id.webview_pembayaran)
                        webView.webViewClient = object: WebViewClient() {
                            override fun doUpdateVisitedHistory(
                                view: WebView?,
                                url: String?,
                                isReload: Boolean
                            ) {
                                Log.d("kencana", "url: " + url.toString())
                                if (url != null) {
                                    if (url.contains("#/406")) {
                                        isProcessed = true
                                        finish()
                                    }
                                }
                                super.doUpdateVisitedHistory(view, url, isReload)
                            }
                        }
                        webView.settings.javaScriptEnabled = true
                        webView.loadUrl(transactionUrl)
                        isProcessed = true
                    }
                    if(response.data.status == Status.DIPROSES){
                        buildUiKit()
                        val midtrans = MidtransSDK.getInstance()
                        val transactionRequest = TransactionRequest(response.data?.kode_pemesanan!!, response.data.total_bayar!!, "IDR")
                        val customerDetail = CustomerDetails()
                        customerDetail.customerIdentifier = response.data.user?.id
                        customerDetail.firstName = response.data.user?.nama
                        customerDetail.email = MyUser.user?.email
                        customerDetail.phone = response.data.user?.no_telp

                        val billingAddress = BillingAddress()
                        billingAddress.address = "";
                        billingAddress.city = "";
                        billingAddress.postalCode = "";
                        customerDetail.billingAddress = billingAddress

                        val shippingAddress = ShippingAddress()
                        shippingAddress.address = ""
                        shippingAddress.city = ""
                        shippingAddress.postalCode = ""
                        customerDetail.shippingAddress = shippingAddress

                        transactionRequest.customerDetails = customerDetail

                        // item details
                        val itemDetails = arrayListOf<ItemDetails>()
                        itemDetails.add(ItemDetails(response.data.produk?.id, response.data.produk?.harga!!, 1, namaProdukPaket))
                        if (!response.data.promo?.kode.isNullOrEmpty()) {
                            val namaPromo =  "Promo Diskon " + response.data.promo?.persen + "%"
                            itemDetails.add(ItemDetails(response.data.promo?.kode, -response.data.promo?.potongan!!, 1, namaPromo))
                        }
                        transactionRequest.itemDetails = itemDetails

                        // set sdk
                        midtrans.transactionRequest = transactionRequest

                        // launch transaction
                        midtrans.startPaymentUiFlow(this)
                    }
                    if(response.data.status == Status.SELESAI) {
                        finish()
                    }


                }
                is Response.Failure -> {
                    makeToast("Error mengambil pemesanan: " + response.errorMessage)
                }

                else -> {}
            }
        }




    }

    private fun buildUiKit() {
        SdkUIFlowBuilder.init()
            .setClientKey("SB-Mid-client-9ISWkdD18OTVrVhh")
            .setContext(this@ProsesTransaksiActivity)
            .setTransactionFinishedCallback { result ->
                makeToast("Transaction Result: " + result.status)
                finish()
            }
            .setMerchantBaseUrl("https://kencana-admin.vercel.app/api/")
            .setColorTheme(CustomColorTheme("#" + Integer.toHexString(ContextCompat.getColor(this@ProsesTransaksiActivity, R.color.md_theme_light_primary)), "#B61548", "#FFE51255"))
            .setLanguage("id")
            .buildSDK()

    }





}