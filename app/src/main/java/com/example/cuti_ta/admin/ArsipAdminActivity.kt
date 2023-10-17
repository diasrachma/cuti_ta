package com.example.cuti_ta.admin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.cuti_ta.R
import com.example.cuti_ta.UrlClass
import com.example.cuti_ta.adapter.AdapterArsipAdmin
import com.example.cuti_ta.pimpinan.ArsipDetailPimpinanActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_arsip_admin.*
import org.json.JSONArray

class ArsipAdminActivity : AppCompatActivity() {

    lateinit var urlClass: UrlClass
    val daftarArsip = mutableListOf<HashMap<String,String>>()
    lateinit var arsipAdapter: AdapterArsipAdmin

    val daftarArsipSelesai = mutableListOf<HashMap<String,String>>()
    lateinit var arsipAdapterSelesai: AdapterArsipSelesai

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arsip_admin)
        supportActionBar?.hide()
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        urlClass = UrlClass()

        arsipUnitKerja.visibility = View.GONE

        showDataArsip("arsip", "")
        showDataArsipSelesai("arsip_selesai", "")

        btnSearchArsipAdmin.setOnClickListener {
            val text = textSearchArsipAdmin.text.toString().trim()
            showDataArsip("arsip", text)
            showDataArsipSelesai("arsip_selesai", text)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun showDataArsip(mode: String, nama: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlCuti,
            Response.Listener { response ->
                daftarArsip.clear()
                if (response.equals(0)) {
                    Toast.makeText(this,"Data tidak ditemukan", Toast.LENGTH_LONG).show()
                } else {
                    val jsonArray = JSONArray(response)
                    for (x in 0..(jsonArray.length()-1)){
                        val jsonObject = jsonArray.getJSONObject(x)
                        var  frm = HashMap<String,String>()
                        frm.put("nama",jsonObject.getString("nama"))
                        frm.put("id_cuti",jsonObject.getString("id_cuti"))
                        frm.put("tanggal_cuti",jsonObject.getString("tanggal_cuti"))
                        frm.put("jenis_cuti",jsonObject.getString("jenis_cuti"))
                        frm.put("status_cuti",jsonObject.getString("status_cuti"))
                        frm.put("foto",jsonObject.getString("foto"))

                        daftarArsip.add(frm)
                    }
                    arsipAdapter.notifyDataSetChanged()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                hm.put("nama",nama)
                when(mode){
                    "arsip" -> {
                        hm.put("mode","arsip")
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun showDataArsipSelesai(mode: String, nama: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlCuti,
            Response.Listener { response ->
                daftarArsipSelesai.clear()
                if (response.equals(0)) {
                    Toast.makeText(this,"Data tidak ditemukan", Toast.LENGTH_LONG).show()
                } else {
                    val jsonArray = JSONArray(response)
                    for (x in 0..(jsonArray.length()-1)){
                        val jsonObject = jsonArray.getJSONObject(x)
                        var  frm = HashMap<String,String>()
                        frm.put("nama",jsonObject.getString("nama"))
                        frm.put("id_cuti",jsonObject.getString("id_cuti"))
                        frm.put("tanggal_cuti",jsonObject.getString("tanggal_cuti"))
                        frm.put("jenis_cuti",jsonObject.getString("jenis_cuti"))
                        frm.put("status_cuti",jsonObject.getString("status_cuti"))
                        frm.put("foto",jsonObject.getString("foto"))

                        daftarArsipSelesai.add(frm)
                    }
                    arsipAdapterSelesai.notifyDataSetChanged()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                hm.put("nama",nama)
                when(mode){
                    "arsip_selesai" -> {
                        hm.put("mode","arsip_selesai")
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    class AdapterArsipSelesai(val dataArsip: List<HashMap<String,String>>) :
        RecyclerView.Adapter<AdapterArsipSelesai.HolderDataArsip>() {
        class HolderDataArsip(v : View) : RecyclerView.ViewHolder(v) {
            val image_cuti = v.findViewById<ImageView>(R.id.imgCutiAdmin)
            val namaAdmin = v.findViewById<TextView>(R.id.tvNamaCutiAdmin)
            val jenis_cuti = v.findViewById<TextView>(R.id.tvJenisCutiAdmin)
            val tanggal_cuti = v.findViewById<TextView>(R.id.tvTanggalCutiAdmin)
            val icon_admin = v.findViewById<ImageView>(R.id.iconAdmin)
            val card = v.findViewById<CardView>(R.id.cardCutiAdmin)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataArsip {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.row_cuti_admin,parent,false)
            return HolderDataArsip(v)
        }

        override fun onBindViewHolder(holder: HolderDataArsip, position: Int) {
            val data = dataArsip.get(position)
            Picasso.get().load(data.get("foto")).into(holder.image_cuti)
            holder.namaAdmin.setText(data.get("nama"))
            holder.jenis_cuti.setText(data.get("jenis_cuti"))
            holder.tanggal_cuti.setText(data.get("tanggal_cuti"))

            val sts = data.get("status_cuti").toString()
            if (sts.equals("Diterima")) {
                holder.icon_admin.setBackgroundResource(R.drawable.success)
            } else if (sts.equals("Pending")) {
                holder.icon_admin.setBackgroundResource(R.drawable.information)
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
            return dataArsip.size
        }
    }
}