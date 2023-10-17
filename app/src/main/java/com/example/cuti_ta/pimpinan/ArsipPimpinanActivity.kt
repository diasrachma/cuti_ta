package com.example.cuti_ta.pimpinan

import android.content.Context
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
import kotlinx.android.synthetic.main.activity_arsip_admin.*
import org.json.JSONArray

class ArsipPimpinanActivity : AppCompatActivity() {

    lateinit var urlClass: UrlClass

    val daftarArsipSelesai = mutableListOf<HashMap<String,String>>()
    lateinit var arsipAdapterSelesai: AdapterArsipPimpinan

    var unitKerja = ""

    lateinit var preferences: SharedPreferences
    val PREF_NAME = "akun"
    val JABATAN = "jabatan"
    val DEF_JABATAN = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arsip_admin)
        supportActionBar?.hide()
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        urlClass = UrlClass()

        arsipAdapterSelesai = AdapterArsipPimpinan(daftarArsipSelesai)
        rvArsipAdmin.layoutManager = LinearLayoutManager(this)
        rvArsipAdmin.adapter = arsipAdapterSelesai

        val jabatanPimpinan = preferences.getString(JABATAN, DEF_JABATAN).toString()

        btnSearchArsipAdmin.setOnClickListener {
            val text = textSearchArsipAdmin.text.toString().trim()
            if (jabatanPimpinan.equals("Ketua") || jabatanPimpinan.equals("Wakil Ketua")) {
                showDataArsipSelesai("arsipPimpinan_selesai", text)
            } else {
                showDataArsipSelesai("arsipPimpinan_selesai", text)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val jabatanPimpinan = preferences.getString(JABATAN, DEF_JABATAN).toString()
        if (jabatanPimpinan.equals("Panitera")) {
            showDataArsipSelesai("arsipPimpinan_selesai", "")
            unitKerja = "Kepaniteraan"
            arsipUnitKerja.setText("Unit Kerja : Kepaniteraan")
        } else if (jabatanPimpinan.equals("Sekretaris")) {
            showDataArsipSelesai("arsipPimpinan_selesai", "")
            unitKerja = "Kesekretariatan"
            arsipUnitKerja.setText("Unit Kerja : Kesekretariatan")
        } else if (jabatanPimpinan.equals("Ketua")) {
            showDataArsipSelesai("arsip_selesai", "")
            unitKerja = ""
            arsipUnitKerja.setText("Unit Kerja : Kesekretariatan/Kepaniteraan")
        } else if (jabatanPimpinan.equals("Wakil Ketua")) {
            showDataArsipSelesai("arsip_selesai", "")
            unitKerja = ""
            arsipUnitKerja.setText("Unit Kerja : Kesekretariatan/Kepaniteraan")
        }
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
                    "arsipPimpinan_selesai" -> {
                        hm.put("mode","arsipPimpinan_selesai")
                        hm.put("unit_kerja", unitKerja)
                    }
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
}