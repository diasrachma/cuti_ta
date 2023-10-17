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
import com.example.cuti_ta.admin.UserAdminActivity
import com.example.cuti_ta.admin.UserDetailAdminFragment
import com.squareup.picasso.Picasso

class AdapterUser(val dataUser: List<HashMap<String,String>>, val thisParent: UserAdminActivity) :
        RecyclerView.Adapter<AdapterUser.HolderDataUser>() {
    class HolderDataUser(v : View) : RecyclerView.ViewHolder(v) {
        val imageUser = v.findViewById<ImageView>(R.id.imgUser)
        val namaUser = v.findViewById<TextView>(R.id.tvNamaUser)
        val jabatanUser = v.findViewById<TextView>(R.id.tvJabatanUser)
        val statusUser = v.findViewById<View>(R.id.stsUser)
        val cardList = v.findViewById<CardView>(R.id.cardUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataUser {
        val v =LayoutInflater.from(parent.context).inflate(R.layout.row_user, parent, false)
        return HolderDataUser(v)
    }

    override fun onBindViewHolder(holder: HolderDataUser, position: Int) {
        val data = dataUser.get(position)
        val img = data.get("foto").toString()
        if (img.equals("null")) {
            Picasso.get().load(R.drawable.user).into(holder.imageUser)
        } else {
            Picasso.get().load(data.get("foto")).into(holder.imageUser)
        }

        val jbt = data.get("jabatan").toString()
        if (jbt.equals("null")) {
            holder.jabatanUser.setText("default")
        } else {
            holder.jabatanUser.setText(data.get("jabatan"))
        }

        holder.namaUser.setText(data.get("nama"))

        val sts = data.get("sts_akun").toString()
        if (sts.equals("AKTIF")) {
            holder.statusUser.visibility = View.VISIBLE
        } else {
            holder.statusUser.visibility = View.GONE
        }

        holder.cardList.setOnClickListener { v : View ->
            var dialog = UserDetailAdminFragment()

            val bundle = Bundle()
            bundle.putString("jabatan", thisParent.jabatanPimpinan)
            bundle.putString("nip", data.get("nip").toString())
            dialog.arguments = bundle

            dialog.show(thisParent.supportFragmentManager, "UserDetailAdminFragment")
        }
    }

    override fun getItemCount(): Int {
        return dataUser.size
    }
}