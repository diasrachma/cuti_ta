package com.example.cuti_ta.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.cuti_ta.R
import com.example.cuti_ta.admin.KuotaAdminActivity
import com.example.cuti_ta.admin.KuotaEditAdminFragment
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class AdapterKuota(val dataSisa: List<HashMap<String, String>>, val thisParent : KuotaAdminActivity)
    : RecyclerView.Adapter<AdapterKuota.HolderDataSisa>() {
    class HolderDataSisa(v : View) : RecyclerView.ViewHolder(v) {
        val imgSisa = v.findViewById<ImageView>(R.id.imgSisa)
        val namaSisa = v.findViewById<TextView>(R.id.tvNamaSisa)
        val sisaPegawai = v.findViewById<TextView>(R.id.tvSisaPegawai)
        val tanggalSisa = v.findViewById<TextView>(R.id.tvTanggalSisa)
        val terpakai = v.findViewById<TextView>(R.id.tvTerpakaiKuota)
        val editKuota = v.findViewById<CircleImageView>(R.id.btnEditKuota)
        val cardSisa = v.findViewById<CardView>(R.id.cardSisa)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataSisa {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_sisa,parent,false)
        return HolderDataSisa(v)
    }

    override fun onBindViewHolder(holder: HolderDataSisa, position: Int) {
        val data = dataSisa.get(position)
        holder.namaSisa.setText(data.get("nama"))
        holder.sisaPegawai.setText("Sisa : "+data.get("sisa_kuota"))
        holder.tanggalSisa.setText(data.get("tahun_kuota"))
        holder.terpakai.setText("Terpakai : "+data.get("terpakai"))

        val img = data.get("foto").toString()
        if (img.equals("null")) {
            Picasso.get().load(R.drawable.user).into(holder.imgSisa)
        } else {
            Picasso.get().load(data.get("foto")).into(holder.imgSisa)
        }

        var kode = data.get("kd_kuota").toString()
        var nama = data.get("nama").toString()
        var sisa = data.get("sisa_kuota").toString()
        var terpakai = data.get("terpakai").toString()

        holder.editKuota.setOnClickListener {
            var dialog = KuotaEditAdminFragment()

            val bundle = Bundle()
            bundle.putString("kode", kode)
            bundle.putString("nama", nama)
            bundle.putString("sisa", sisa)
            bundle.putString("terpakai", terpakai)
            dialog.arguments = bundle

            dialog.show(thisParent.supportFragmentManager, "KuotaEditAdminFragment")
        }

        holder.cardSisa.setOnClickListener { v : View ->
//            val intentDetail = Intent(v.context, DetailSisaActivity::class.java)
//            intentDetail.putExtra("nama",data.get("nama"))
//            v.context.startActivity(intentDetail)
        }
    }

    override fun getItemCount(): Int {
        return dataSisa.size
    }
}