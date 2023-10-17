package com.example.cuti_ta.pegawai

import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.cuti_ta.R
import com.example.cuti_ta.UrlClass
import com.example.cuti_ta.adapter.AdapterIzinArsip
import kotlinx.android.synthetic.main.activity_izin_riwayat.*
import org.json.JSONArray

class IzinArsipPegawaiActivity : AppCompatActivity() {
    lateinit var urlClass: UrlClass

    val daftarIzin = mutableListOf<HashMap<String,String>>()
    lateinit var izinAdapter : AdapterIzinArsip

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_izin_riwayat)
        supportActionBar?.setTitle("Arsip Izin Keluar Pegawai")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        izinAdapter = AdapterIzinArsip(daftarIzin)
        rvIzinRiwayat.layoutManager = LinearLayoutManager(this)
        rvIzinRiwayat.adapter = izinAdapter

        urlClass = UrlClass()

        btnCariIzin.setOnClickListener {
            showDataIzin("read_arsip", textCariIzin.text.toString().trim())
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onStart() {
        super.onStart()
        showDataIzin("read_arsip", "")
    }

    private fun showDataIzin(mode: String, nama: String) {
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
                        frm.put("img_profil",jsonObject.getString("img_profil"))
                        frm.put("nama",jsonObject.getString("nama"))
                        frm.put("id_izinkeluar",jsonObject.getString("id_izinkeluar"))
                        frm.put("tgl_izinkeluar",jsonObject.getString("tgl_izinkeluar"))
                        frm.put("jam_izinkeluar",jsonObject.getString("jam_izinkeluar"))

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
                    "read_arsip" -> {
                        hm.put("mode","read_arsip")
                        hm.put("nama", nama)
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}