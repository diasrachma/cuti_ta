package com.example.cuti_ta.pimpinan

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.example.cuti_ta.adapter.AdapterArsipPimpinan
import kotlinx.android.synthetic.main.activity_riwayat_admin.*
import org.json.JSONArray

class ArsipPendingPimpinanActivity : AppCompatActivity() {

    lateinit var urlClass: UrlClass

    var unitKerja = ""

    lateinit var preferences: SharedPreferences
    val PREF_NAME = "akun"
    val JABATAN = "jabatan"
    val DEF_JABATAN = ""

    val daftarArsip = mutableListOf<HashMap<String,String>>()
    lateinit var arsipAdapter: AdapterArsipPimpinan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat_admin)
        supportActionBar?.hide()
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        urlClass = UrlClass()

        arsipAdapter = AdapterArsipPimpinan(daftarArsip)
        rvCutiMainAdmin.layoutManager = LinearLayoutManager(this)
        rvCutiMainAdmin.adapter = arsipAdapter

        val jabatanPimpinan = preferences.getString(JABATAN, DEF_JABATAN).toString()

        btnSearchMainAdmin.setOnClickListener {
            val text = textSearchMainAdmin.text.toString().trim()
            if (jabatanPimpinan.equals("Ketua") || jabatanPimpinan.equals("Wakil Ketua")) {
                showDataArsip("arsipKetua_proses", text)
            } else {
                showDataArsip("arsipPimpinan_proses", text)
            }
        }

        btnArsip.setOnClickListener {
            startActivity(Intent(this, ArsipPimpinanActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        val jabatanPimpinan = preferences.getString(JABATAN, DEF_JABATAN).toString()
        if (jabatanPimpinan.equals("Panitera")) {
            showDataArsip("arsipPimpinan_proses", "")
            unitKerja = "Kepaniteraan"
            judul.setText("Unit Kerja : Kepaniteraan")
        } else if (jabatanPimpinan.equals("Sekretaris")) {
            showDataArsip("arsipPimpinan_proses", "")
            unitKerja = "Kesekretariatan"
            judul.setText("Unit Kerja : Kesekretariatan")
        } else if (jabatanPimpinan.equals("Ketua")) {
            showDataArsip("arsipKetua", "")
            unitKerja = ""
            judul.setText("Unit Kerja : Kesekretariatan/Kepaniteraan")
        } else if (jabatanPimpinan.equals("Wakil Ketua")) {
            showDataArsip("arsip", "")
            unitKerja = ""
            judul.setText("Unit Kerja : Kesekretariatan/Kepaniteraan")
        }
    }

    fun showDataArsip(mode: String, nama: String) {
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
                    "arsipPimpinan_proses" -> {
                        hm.put("mode","arsipPimpinan_proses")
                        hm.put("unit_kerja", unitKerja)
                    }
                    "arsipKetua_proses" -> {
                        hm.put("mode","arsipKetua_proses")
                        hm.put("unit_kerja", unitKerja)
                    }
                    "arsip" -> {
                        hm.put("mode","arsip")
                    }
                    "arsipKetua" -> {
                        hm.put("mode","arsipKetua")
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}