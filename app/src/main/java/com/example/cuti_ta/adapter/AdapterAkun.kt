package com.example.cuti_ta.adapter

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.cuti_ta.R
import com.example.cuti_ta.admin.AkunAdminActivity
import com.example.cuti_ta.admin.AkunDetailAdminFragment

class AdapterAkun(val dataAkun: List<HashMap<String, String>>, val thisParent: AkunAdminActivity)
    : RecyclerView.Adapter<AdapterAkun.HolderDataAkun>(){
    class HolderDataAkun(v : View) : RecyclerView.ViewHolder(v) {
        val stsAkun = v.findViewById<TextView>(R.id.tvStsAkun)
        val usernameAkun = v.findViewById<TextView>(R.id.tvUsernameAkun)
        val passwordAkun = v.findViewById<TextView>(R.id.tvPasswordAKun)
        val statusAkun = v.findViewById<TextView>(R.id.tvStatusAkun)
        val card = v.findViewById<CardView>(R.id.cardAkun)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataAkun {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_akun,parent,false)
        return HolderDataAkun(v)
    }

    override fun onBindViewHolder(holder: HolderDataAkun, position: Int) {
        val data = dataAkun.get(position)
        holder.usernameAkun.setText("Username : "+data.get("username"))
        holder.passwordAkun.setText("Password : "+data.get("password"))
        holder.statusAkun.setText(data.get("level"))

        val sts = data.get("sts_akun")
        if (sts.toString().equals("AKTIF")) {
            holder.stsAkun.setText("Aktif")
            holder.card?.setCardBackgroundColor(Color.parseColor("#CEEDC7"))
        } else {
            holder.stsAkun.setText("NonAktif")
            holder.card?.setCardBackgroundColor(Color.parseColor("#FF7C7C"))
        }

        holder.card.setOnClickListener {
            var dialog = AkunDetailAdminFragment()
            var paket : Bundle? = thisParent.intent.extras
            var username = paket?.getString("username")

            val bundle = Bundle()
            bundle.putString("username", username)
            bundle.putString("username", data.get("username").toString())
            dialog.arguments = bundle

            dialog.show(thisParent.supportFragmentManager, "AkunDetailAdminFragment")
        }

//        if(position.rem(2)==0) holder.card?.setCardBackgroundColor(Color.parseColor("#EAFDFC"))
//        else holder.card?.setCardBackgroundColor(Color.parseColor("#CEEDC7"))
    }

    override fun getItemCount(): Int {
        return dataAkun.size
    }
}