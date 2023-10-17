package com.example.cuti_ta.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.cuti_ta.R
import com.example.cuti_ta.pimpinan.ArsipDetailPimpinanActivity
import com.squareup.picasso.Picasso

class AdapterArsipPimpinan(val dataCuti: List<HashMap<String,String>>)
    : RecyclerView.Adapter<AdapterArsipPimpinan.HolderDataCuti>() {
    class HolderDataCuti(v : View) : RecyclerView.ViewHolder(v) {
        val image_cuti = v.findViewById<ImageView>(R.id.imgCutiAdmin)
        val namaAdmin = v.findViewById<TextView>(R.id.tvNamaCutiAdmin)
        val jenis_cuti = v.findViewById<TextView>(R.id.tvJenisCutiAdmin)
        val tanggal_cuti = v.findViewById<TextView>(R.id.tvTanggalCutiAdmin)
        val icon_admin = v.findViewById<ImageView>(R.id.iconAdmin)
        val card = v.findViewById<CardView>(R.id.cardCutiAdmin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataCuti {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_cuti_admin,parent,false)
        return HolderDataCuti(v)
    }

    override fun onBindViewHolder(holder: HolderDataCuti, position: Int) {
        val data = dataCuti.get(position)
        Picasso.get().load(data.get("foto")).into(holder.image_cuti)
        holder.namaAdmin.setText(data.get("nama"))
        holder.jenis_cuti.setText(data.get("jenis_cuti"))
        holder.tanggal_cuti.setText(data.get("tanggal_cuti"))

        val sts = data.get("status_cuti").toString()
        if (sts.equals("Diterima")) {
            holder.icon_admin.setBackgroundResource(R.drawable.success)
        } else if (sts.equals("Pending")) {
            holder.icon_admin.setBackgroundResource(R.drawable.information)
        } else if (sts.equals("Pending1")) {
            holder.icon_admin.setBackgroundResource(R.drawable.progress)
        } else {
            holder.icon_admin.setBackgroundResource(R.drawable.cancel)
        }

        holder.card.setOnClickListener{ v : View ->
            val intentDetail = Intent(v.context, ArsipDetailPimpinanActivity::class.java)
            intentDetail.putExtra("idCuti",data.get("id_cuti"))
            v.context.startActivity(intentDetail)
        }
    }

    override fun getItemCount(): Int {
        return dataCuti.size
    }
}