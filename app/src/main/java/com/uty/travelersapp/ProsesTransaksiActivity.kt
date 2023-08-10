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
    private var paymentUrl = ""
    private val pemesananViewModel by viewModels<PemesananViewModel>()
    private var isProcessed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proses_transaksi)

        val loadingSpinner = Helper.buildLoadingSpinner(this, "Loading", "Memuat pemesanan...")
        
        val i = intent.getStringExtra(IntentKey.PAYMENT_URL)
        if (i != null) {
            paymentUrl = i
        }

        if (paymentUrl.isEmpty()) {
            makeToast("Pemesanan tidak ditemukan")
            finish()
        } else {
            val transactionUrl = paymentUrl
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


    }

    private fun buildUiKit() {
        SdkUIFlowBuilder.init()
            .setClientKey("SB-Mid-client-9ISWkdD18OTVrVhh")
            .setContext(this@ProsesTransaksiActivity)
            .setTransactionFinishedCallback { result ->
//                makeToast("Transaction Result: " + result.status)
                isProcessed = true
                finish()
            }
            .setMerchantBaseUrl("https://kencana-admin.vercel.app/api/")
            .setColorTheme(CustomColorTheme("#" + Integer.toHexString(ContextCompat.getColor(this@ProsesTransaksiActivity, R.color.md_theme_light_primary)), "#B61548", "#FFE51255"))
            .setLanguage("id")
//            .enableLog(true)
            .buildSDK()

    }





}