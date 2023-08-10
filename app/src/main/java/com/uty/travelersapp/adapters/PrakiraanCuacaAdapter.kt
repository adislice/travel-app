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
import com.uty.travelersapp.utils.Helper

class PrakiraanCuacaAdapter(): RecyclerView.Adapter<PrakiraanCuacaAdapter.PrakiraanCuacaViewHolder>() {

    var prakiraanCuacaList = listOf<ForecastData>()

    class PrakiraanCuacaViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val container = itemView
        val imgCuaca = itemView.findViewById<ImageView>(R.id.img_cuaca)
        val txtWaktu = itemView.findViewById<TextView>(R.id.txt_waktu)
        val txtDeskripsi = itemView.findViewById<TextView>(R.id.txt_deskripsi)
        val txtSuhu = itemView.findViewById<TextView>(R.id.txt_suhu)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrakiraanCuacaViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_prakiraan_cuaca, parent, false)
        return PrakiraanCuacaViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return prakiraanCuacaList.size
    }

    override fun onBindViewHolder(holder: PrakiraanCuacaViewHolder, position: Int) {
        val data = prakiraanCuacaList[position]
        val imgurl = "https://kencana-admin.vercel.app/api/weather-icon?icon=${data.weather?.get(0)?.icon}"
        Log.d("kencana", "icon: "+imgurl)
        holder.imgCuaca.load(imgurl) {
            crossfade(true)
            placeholder(R.drawable.loading_image_placeholder)
        }
        holder.txtWaktu.text = data.dt?.let { Helper.convertUnixTimestampToFormattedDate(it) }
        holder.txtDeskripsi.text = data.weather?.get(0)?.description
        holder.txtSuhu.text = data.main?.temp.toString() + " °C"

    }

    fun updateList(newList: List<ForecastData>) {
        prakiraanCuacaList = newList
        notifyDataSetChanged()
    }
}