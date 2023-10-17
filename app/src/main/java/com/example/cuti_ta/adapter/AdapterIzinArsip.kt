package com.example.cuti_ta.adapter

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.cuti_ta.R
import com.example.cuti_ta.admin.IzinDetailAdminActivity
import com.squareup.picasso.Picasso

class AdapterIzinArsip (val dataIzin : List<HashMap<String,String>>)
    : RecyclerView.Adapter<AdapterIzinArsip.HolderDataIzin>(){
    class HolderDataIzin(v: View) : RecyclerView.ViewHolder(v) {
        val namaIzin = v.findViewById<TextView>(R.id.adpIzinNama)
        val tglIzin = v.findViewById<TextView>(R.id.adpIzinTgl)
        val jamIzin = v.findViewById<TextView>(R.id.adpIzinJam)
        val img = v.findViewById<ImageView>(R.id.adpImgIzin)
        val card = v.findViewById<CardView>(R.id.cardIzin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataIzin {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_izin_arsip, parent, false)
        return  HolderDataIzin(v)
    }

    override fun getItemCount(): Int {
        return dataIzin.size
    }

    override fun onBindViewHolder(holder: HolderDataIzin, position: Int) {
        val data = dataIzin.get(position)
        holder.namaIzin.setText(data.get("nama"))
        Picasso.get().load(data.get("img_profil")).into(holder.img)
        holder.jamIzin.setText(data.get("jam_izinkeluar"))
        holder.tglIzin.setText(data.get("tgl_izinkeluar"))

        holder.card.setOnClickListener {
            val intent = Intent(it.context, IzinDetailAdminActivity::class.java)
            intent.putExtra("idIzin", data.get("id_izinkeluar"))
            it.context.startActivity(intent)
        }
    }
}