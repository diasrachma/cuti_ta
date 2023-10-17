package com.example.cuti_ta.pegawai

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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
import kotlinx.android.synthetic.main.activity_riwayat.*
import org.json.JSONArray

class RiwayatPegawaiActivity : AppCompatActivity() {

    lateinit var urlClass: UrlClass
    val daftarCuti = mutableListOf<HashMap<String,String>>()
    val daftarCuti2 = mutableListOf<HashMap<String,String>>()
    lateinit var cutiAdapter: AdapterCuti
    lateinit var cutiAdapter2: AdapterCuti2

    lateinit var preferences: SharedPreferences

    val PREF_NAME = "akun"
    val USERNAME = "username"
    val DEF_USERNAME = ""
    val PASSWORD = "password"
    val DEF_PASSWORD = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat)
        supportActionBar?.setTitle("Riwayat Cuti Anda")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        urlClass = UrlClass()
        preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        cutiAdapter = AdapterCuti(daftarCuti)
        rvRiwayatProses.layoutManager = LinearLayoutManager(this)
        rvRiwayatProses.adapter = cutiAdapter

        cutiAdapter2 = AdapterCuti2(daftarCuti2)
        rvRiwayatSelesai.layoutManager = LinearLayoutManager(this)
        rvRiwayatSelesai.adapter = cutiAdapter2
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onStart() {
        super.onStart()
        showDataCuti("proses")
        showDataCuti2("selesai")
    }

    private fun showDataCuti2(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlCuti,
            Response.Listener { response ->
                daftarCuti2.clear()
                if (response.equals(0)) {
                    Toast.makeText(this,"Data tidak ditemukan", Toast.LENGTH_LONG).show()
                } else {
                    val jsonArray = JSONArray(response)
                    for (x in 0..(jsonArray.length()-1)){
                        val jsonObject = jsonArray.getJSONObject(x)
                        var  frm = HashMap<String,String>()
                        frm.put("id_cuti",jsonObject.getString("id_cuti"))
                        frm.put("tanggal_cuti",jsonObject.getString("tanggal_cuti"))
                        frm.put("jenis_cuti",jsonObject.getString("jenis_cuti"))
                        frm.put("lama_cuti",jsonObject.getString("lama_cuti"))
                        frm.put("status_cuti",jsonObject.getString("status_cuti"))

                        daftarCuti2.add(frm)
                    }
                    cutiAdapter2.notifyDataSetChanged()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                hm.put("username",preferences.getString(USERNAME, DEF_USERNAME).toString())
                hm.put("password",preferences.getString(PASSWORD, DEF_PASSWORD).toString())
                when(mode){
                    "selesai" -> {
                        hm.put("mode","selesai")
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    private fun showDataCuti(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlCuti,
            Response.Listener { response ->
                daftarCuti.clear()
                if (response.equals(0)) {
                    Toast.makeText(this,"Data tidak ditemukan", Toast.LENGTH_LONG).show()
                } else {
                    val jsonArray = JSONArray(response)
                    for (x in 0..(jsonArray.length()-1)){
                        val jsonObject = jsonArray.getJSONObject(x)
                        var  frm = HashMap<String,String>()
                        frm.put("id_cuti",jsonObject.getString("id_cuti"))
                        frm.put("tanggal_cuti",jsonObject.getString("tanggal_cuti"))
                        frm.put("jenis_cuti",jsonObject.getString("jenis_cuti"))
                        frm.put("lama_cuti",jsonObject.getString("lama_cuti"))
                        frm.put("status_cuti",jsonObject.getString("status_cuti"))

                        daftarCuti.add(frm)
                    }
                    cutiAdapter.notifyDataSetChanged()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                hm.put("username",preferences.getString(USERNAME, DEF_USERNAME).toString())
                hm.put("password",preferences.getString(PASSWORD, DEF_PASSWORD).toString())
                when(mode){
                    "proses" -> {
                        hm.put("mode","proses")
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    class AdapterCuti(val dataCuti: List<HashMap<String,String>>)
        : RecyclerView.Adapter<AdapterCuti.HolderDataCuti>() {
        class HolderDataCuti(v: View) : RecyclerView.ViewHolder(v) {
            val tanggal_cuti = v.findViewById<TextView>(R.id.adpTanggal)
            val id_cuti = v.findViewById<TextView>(R.id.adpId)
            val jenis_cuti = v.findViewById<TextView>(R.id.adpJenisCuti)
            val lama_cuti = v.findViewById<TextView>(R.id.adpLamaCuti)
            val status_cuti = v.findViewById<TextView>(R.id.adpStatus)
            val card = v.findViewById<CardView>(R.id.cardCuti)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataCuti {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.row_cuti, parent, false)
            return HolderDataCuti(v)
        }

        override fun getItemCount(): Int {
            return dataCuti.size
        }

        override fun onBindViewHolder(holder: HolderDataCuti, position: Int) {
            val data = dataCuti.get(position)
            val sts = data.get("status_cuti").toString()

            holder.tanggal_cuti.setText(data.get("tanggal_cuti"))
            holder.id_cuti.setText(data.get("id_cuti"))
            holder.jenis_cuti.setText(data.get("jenis_cuti"))
            holder.lama_cuti.setText(data.get("lama_cuti"))

            if (sts.equals("Pending")) {
                holder.card.setCardBackgroundColor(Color.parseColor("#BAD6FF"))
                holder.status_cuti.setText("Pending")
            } else if (sts.equals("Pending1")) {
                holder.card.setCardBackgroundColor(Color.parseColor("#FFC107"))
                holder.status_cuti.setText("Acc Pimpinan")
            } else if (sts.equals("Diterima")) {
                holder.card.setCardBackgroundColor(Color.parseColor("#ABE8A1"))
                holder.status_cuti.setText("Diterima")
            } else {
                holder.card.setCardBackgroundColor(Color.parseColor("#FF7C7C"))
                holder.status_cuti.setText("Ditangguhkan")
            }

            holder.card.setOnClickListener { v: View ->
                val intentDetail = Intent(v.context, RiwayatDetailPegawaiActivity::class.java)
                intentDetail.putExtra("idCuti",data.get("id_cuti"))
                v.context.startActivity(intentDetail)
            }
        }
    }

    class AdapterCuti2(val dataCuti: List<HashMap<String,String>>) : RecyclerView.Adapter<AdapterCuti2.HolderDataCuti>() {
        class HolderDataCuti(v : View) : RecyclerView.ViewHolder(v) {
            val tanggal_cuti = v.findViewById<TextView>(R.id.adpTanggal)
            val id_cuti = v.findViewById<TextView>(R.id.adpId)
            val jenis_cuti = v.findViewById<TextView>(R.id.adpJenisCuti)
            val lama_cuti = v.findViewById<TextView>(R.id.adpLamaCuti)
            val status_cuti = v.findViewById<TextView>(R.id.adpStatus)
            val card = v.findViewById<CardView>(R.id.cardCuti)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataCuti {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.row_cuti,parent,false)
            return HolderDataCuti(v)
        }

        override fun onBindViewHolder(holder: HolderDataCuti, position: Int) {
            val data = dataCuti.get(position)

            holder.tanggal_cuti.setText(data.get("tanggal_cuti"))
            holder.id_cuti.setText(data.get("id_cuti"))
            holder.jenis_cuti.setText(data.get("jenis_cuti"))
            holder.lama_cuti.setText(data.get("lama_cuti"))

            val sts = data.get("status_cuti").toString()
            if (sts.equals("Pending")){
                holder.card.setCardBackgroundColor(Color.parseColor("#BAD6FF"))
                holder.status_cuti.setText("Pending")
            } else if (sts.equals("Pending1")) {
                holder.card.setCardBackgroundColor(Color.parseColor("#FFC107"))
                holder.status_cuti.setText("Acc Pimpinan")
            } else if (sts.equals("Diterima")) {
                holder.card.setCardBackgroundColor(Color.parseColor("#ABE8A1"))
                holder.status_cuti.setText("Diterima")
            } else {
                holder.card.setCardBackgroundColor(Color.parseColor("#FF7C7C"))
                holder.status_cuti.setText("Ditangguhkan")
            }

            holder.card.setOnClickListener { v : View ->
                val intentDetail = Intent(v.context, RiwayatDetailPegawaiActivity::class.java)
                intentDetail.putExtra("idCuti",data.get("id_cuti"))
                v.context.startActivity(intentDetail)
            }
        }

        override fun getItemCount(): Int {
            return dataCuti.size
        }

    }
}