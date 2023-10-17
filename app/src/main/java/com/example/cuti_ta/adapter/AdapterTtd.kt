package com.example.cuti_ta.adapter

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.cuti_ta.R
import com.example.cuti_ta.admin.TtdAdminActivity
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class AdapterTtd(val dataTtd: List<HashMap<String, String>>, val parent : TtdAdminActivity)
    : RecyclerView.Adapter<AdapterTtd.HolderDataTtd>(){
    class HolderDataTtd(v : View) : RecyclerView.ViewHolder(v) {
        val jabatanTtd = v.findViewById<TextView>(R.id.tvJabatanTtd)
        val namaTtd = v.findViewById<TextView>(R.id.tvNamaTtd)
        val nipTtd = v.findViewById<TextView>(R.id.tvNipTtd)
        val imgTtd = v.findViewById<ImageView>(R.id.imgTtd)
        val deleteTtd = v.findViewById<CircleImageView>(R.id.btnDeleteTtd)
        val card = v.findViewById<CardView>(R.id.cardTtd)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataTtd {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_ttd,parent,false)
        return HolderDataTtd(v)
    }

    override fun onBindViewHolder(holder: HolderDataTtd, position: Int) {
        val data = dataTtd.get(position)
        holder.jabatanTtd.setText(data.get("jabatan"))
        holder.namaTtd.setText(data.get("nama"))
        holder.nipTtd.setText(data.get("nip"))
        Picasso.get().load(data.get("img_ttd")).into(holder.imgTtd)

        if (parent.jabatanPimpinan.equals("Admin")) {
            holder.deleteTtd.visibility = View.VISIBLE
        } else {
            holder.deleteTtd.visibility = View.GONE
        }

        holder.deleteTtd.setOnClickListener {v : View ->
            parent.idTtd = data.get("id_ttd").toString()

            AlertDialog.Builder(v.context)
                .setIcon(R.drawable.warning)
                .setTitle("Peringatan!")
                .setMessage("Apakah Anda ingin menghapus Tanda Tangan "+holder.namaTtd.text.toString())
                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                    parent.deleteTtd("deleteTtd")
                    val intent = Intent(v.context, TtdAdminActivity::class.java)
                    v.context.startActivity(intent)
                    Toast.makeText(v.context, "Berhasil menghapus Tanda Tangan "+holder.namaTtd.text.toString(), Toast.LENGTH_LONG).show()
                })
                .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->
                    Toast.makeText(v.context, "Membatalkan penghapusan Tanda Tangan "+holder.namaTtd.text.toString(), Toast.LENGTH_LONG).show()
                })
                .show()
        }

        if(position.rem(2)==0) holder.card?.setCardBackgroundColor(Color.parseColor("#DDDDDD"))
        else holder.card?.setCardBackgroundColor(Color.parseColor("#EEEEEE"))
    }

    override fun getItemCount(): Int {
        return dataTtd.size
    }
}