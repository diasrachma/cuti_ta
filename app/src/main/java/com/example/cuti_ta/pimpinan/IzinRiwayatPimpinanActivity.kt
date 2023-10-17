package com.example.cuti_ta.pimpinan

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
import kotlinx.android.synthetic.main.activity_izin_riwayat.*
import org.json.JSONArray

class IzinRiwayatPimpinanActivity : AppCompatActivity() {
    lateinit var urlClass: UrlClass
    lateinit var preferences: SharedPreferences

    val PREF_NAME = "akun"
    val USERNAME = "username"
    val DEF_USERNAME = ""
    val PASSWORD = "password"
    val DEF_PASSWORD = ""

    val daftarIzin = mutableListOf<HashMap<String,String>>()
    lateinit var izinAdapter: AdapterIzin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_izin_riwayat)
        supportActionBar?.setTitle("Riwayat Izin Keluar")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        urlClass = UrlClass()
        preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        izinAdapter = AdapterIzin(daftarIzin)
        rvIzinRiwayat.layoutManager = LinearLayoutManager(this)
        rvIzinRiwayat.adapter = izinAdapter

        cariIzin.visibility = View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onStart() {
        super.onStart()
        showDataIzin("read_all")
    }

    private fun showDataIzin(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlIzin,
            Response.Listener { response ->
                daftarIzin.clear()
                if (response.equals(0)) {
                    Toast.makeText(this,"Data tidak ditemukan", Toast.LENGTH_LONG).show()
                } else {
                    val jsonArray = JSONArray(response)
                    for (x in 0..(jsonArray.length()-1)){
                        val jsonObject = jsonArray.getJSONObject(x)
                        var  frm = HashMap<String,String>()
                        frm.put("id_izinkeluar",jsonObject.getString("id_izinkeluar"))
                        frm.put("tgl_izinkeluar",jsonObject.getString("tgl_izinkeluar"))
                        frm.put("jam_izinkeluar",jsonObject.getString("jam_izinkeluar"))
                        frm.put("status_izinkeluar",jsonObject.getString("status_izinkeluar"))

                        daftarIzin.add(frm)
                    }
                    izinAdapter.notifyDataSetChanged()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                when(mode){
                    "read_all" -> {
                        hm.put("mode","read_all")
                        hm.put("username",preferences.getString(USERNAME, DEF_USERNAME).toString())
                        hm.put("password",preferences.getString(PASSWORD, DEF_PASSWORD).toString())
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    class AdapterIzin(val dataIzin : List<HashMap<String,String>>)
        : RecyclerView.Adapter<AdapterIzin.HolderDataIzin>(){
        class HolderDataIzin(v: View) : RecyclerView.ViewHolder(v) {
            val idIzin = v.findViewById<TextView>(R.id.adpIdIzin)
            val tglIzin = v.findViewById<TextView>(R.id.adpTglIzin)
            val jamIzin = v.findViewById<TextView>(R.id.adpJamIzin)
            val stsIzin = v.findViewById<TextView>(R.id.adpStatusIzin)
            val card = v.findViewById<CardView>(R.id.card)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataIzin {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.row_izin, parent, false)
            return  HolderDataIzin(v)
        }

        override fun getItemCount(): Int {
            return dataIzin.size
        }

        override fun onBindViewHolder(holder: HolderDataIzin, position: Int) {
            val data = dataIzin.get(position)
            holder.idIzin.setText(data.get("id_izinkeluar"))
            holder.jamIzin.setText(data.get("jam_izinkeluar"))
            holder.tglIzin.setText(data.get("tgl_izinkeluar"))

            if(position.rem(2)==0) holder.card.setCardBackgroundColor(Color.parseColor("#e2ff98"))
            else holder.card.setCardBackgroundColor(Color.parseColor("#9fffff"))

            val sts = data.get("status_izinkeluar").toString()
            if (sts.equals("Selesai")) {
                holder.stsIzin.setText(data.get("status_izinkeluar"))
                holder.stsIzin.setTextColor(Color.parseColor("#03C988"))
            } else {
                holder.stsIzin.setText(data.get("status_izinkeluar"))
                holder.stsIzin.setTextColor(Color.parseColor("#1D267D"))
            }

            holder.card.setOnClickListener {
                val intent = Intent(it.context, IzinDetailPimpinanActivity::class.java)
                intent.putExtra("idIzin", data.get("id_izinkeluar"))
                it.context.startActivity(intent)
            }
        }
    }
}
