package com.uty.travelersapp.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.card.MaterialCardView
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.uty.travelersapp.PaketWisataBaseActivity
import com.uty.travelersapp.R
import com.uty.travelersapp.models.ProdukPaketWisata
import com.uty.travelersapp.models.Response
import com.uty.travelersapp.utils.IntentKey
import com.uty.travelersapp.viewmodel.PaketWisataViewModel


class PemesananBottomSheet : BottomSheetDialogFragment() {
    private val paketWisataViewModel by activityViewModels<PaketWisataViewModel>()
    private var paketWisataId = ""
    private var produkList = mutableMapOf<String, MaterialCardView>()
    private var selectedId: String = ""
    private var selectedProduk = MutableLiveData<ProdukPaketWisata>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mArgs = arguments
        if (mArgs != null) {
            this.paketWisataId = mArgs.getString("PAKET_WISATA_ID", "")
        }
        val contextThemeWrapper = ContextThemeWrapper(activity, R.style.Theme_TravelersApp)
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.modal_pemesanan_bottom_sheet, container, false)
        return inflater.cloneInContext(contextThemeWrapper).inflate(R.layout.modal_pemesanan_bottom_sheet, container, false)
    }

    companion object {
        const val TAG = "PemesananBottomSheet"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val llContainer = view.findViewById<LinearLayout>(R.id.ll_container_produk)

        val windowInsetsController = WindowCompat.getInsetsController(
            requireActivity().window,
            requireActivity().window.decorView
        )
        windowInsetsController.isAppearanceLightNavigationBars = true

        val tvSelectedId = view.findViewById<TextView>(R.id.tv_selectedId)
        val btnLanjutPesan = view.findViewById<Button>(R.id.btn_pesan_produk_paket)
        btnLanjutPesan.text = "Pesan"
        tvSelectedId.text = "produk id terpilih : "
        selectedProduk.observe(viewLifecycleOwner) { produk ->
            tvSelectedId.text = "produk id terpilih : " + produk.id
            btnLanjutPesan.text = "Pesan " + produk.nama
            selectedId = produk.id!!
        }
        btnLanjutPesan.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(IntentKey.PRODUK_PAKET_WISATA_ID, selectedId)
            bundle.putString(IntentKey.PAKET_WISATA_ID, this.paketWisataId)
            findNavController().navigate(R.id.action_detailPaketWisataFragment_to_pesanPaketWisataFragment, bundle)
        }

        paketWisataViewModel.setPaketWisataId(this.paketWisataId)
        paketWisataViewModel.getProdukPaketWisata.observe(viewLifecycleOwner) { response ->
            when(response){
                is Response.Success -> {
                    Log.d("kencana", "produk: " + response.data.toString())
                    response.data.forEach { produk ->
                        val infl = layoutInflater.inflate(R.layout.cardview_produk_paket_wisata, null, false)
                        infl.findViewById<TextView>(R.id.nama_paket).text = produk.jenis_armada?.kapasitas_min.toString() + " - " + produk.jenis_armada?.kapasitas_max.toString() + ""
                        infl.findViewById<TextView>(R.id.harga_paket).text = "Rp. " + produk.harga.toString()
                        infl.findViewById<TextView>(R.id.nama_kendaraan).text = produk.jenis_armada?.nama
                        infl.findViewById<TextView>(R.id.kapasitas_penumpang).text = produk.jenis_armada?.kapasitas_min.toString() + " - " + produk.jenis_armada?.kapasitas_max.toString() + " orang"
                        val cardview = infl.findViewById<MaterialCardView>(R.id.card_produk_paket_wisata)
                        cardview.setOnClickListener {
                            produkList.forEach {
                                it.value.isChecked = false
                            }
                            produkList[produk.id!!]?.isChecked = true
                            selectedId = produk.id!!
                            selectedProduk.value = produk
                        }
                        produkList[produk.id!!] = cardview
                        llContainer.addView(infl)
                    }
                }
                is Response.Failure -> {
                    Log.d("kencana", "error: " + response.errorMessage)
                }
                else -> {}
            }
        }

        btnLanjutPesan.isEnabled = false
        selectedProduk.observe(viewLifecycleOwner) {sel ->
            btnLanjutPesan.isEnabled = sel.id == selectedId
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        (dialog as BottomSheetDialog).behavior.addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState === BottomSheetBehavior.STATE_EXPANDED) {
                    //In the EXPANDED STATE apply a new MaterialShapeDrawable with rounded cornes
                    val newMaterialShapeDrawable: MaterialShapeDrawable =
                        createMaterialShapeDrawable(bottomSheet)
                    ViewCompat.setBackground(bottomSheet, newMaterialShapeDrawable)
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

        })

        return dialog

    }

    fun createMaterialShapeDrawable(bottomSheet: View): MaterialShapeDrawable {
        val shapeAppearanceModel =
            //Create a ShapeAppearanceModel with the same shapeAppearanceOverlay used in the style
            ShapeAppearanceModel.builder(
                context,
                0,
                R.style.ShapeAppearance_App_LargeComponent
            )
                .build()

        //Create a new MaterialShapeDrawable (you can't use the original MaterialShapeDrawable in the BottomSheet)

        //Create a new MaterialShapeDrawable (you can't use the original MaterialShapeDrawable in the BottomSheet)
        val currentMaterialShapeDrawable = bottomSheet.background as MaterialShapeDrawable
        val newMaterialShapeDrawable = MaterialShapeDrawable(shapeAppearanceModel)

        //Copy the attributes in the new MaterialShapeDrawable

        //Copy the attributes in the new MaterialShapeDrawable
        newMaterialShapeDrawable.initializeElevationOverlay(context)
        newMaterialShapeDrawable.fillColor = currentMaterialShapeDrawable.fillColor
        newMaterialShapeDrawable.tintList = currentMaterialShapeDrawable.tintList
        newMaterialShapeDrawable.elevation = currentMaterialShapeDrawable.elevation
        newMaterialShapeDrawable.strokeWidth = currentMaterialShapeDrawable.strokeWidth
        newMaterialShapeDrawable.strokeColor = currentMaterialShapeDrawable.strokeColor
        return newMaterialShapeDrawable
    }

    override fun onDestroyView() {
        super.onDestroyView()
        selectedProduk.removeObservers(viewLifecycleOwner)
    }

}