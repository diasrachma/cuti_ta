package com.example.cuti_ta.admin

import android.content.Intent
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

class RiwayatAdminActivity : AppCompatActivity() {
    lateinit var urlClass: UrlClass

    val daftarCuti = mutableListOf<HashMap<String,String>>()
    lateinit var cutiAdapter: AdapterArsipPimpinan

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat_admin)
        supportActionBar?.hide()
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        urlClass = UrlClass()

        cutiAdapter = AdapterArsipPimpinan(daftarCuti)
        rvCutiMainAdmin.layoutManager = LinearLayoutManager(this)
        rvCutiMainAdmin.adapter = cutiAdapter

        btnSearchMainAdmin.setOnClickListener {
            showDataCuti("pending", textSearchMainAdmin.text.toString().trim())
        }

        btnArsip.setOnClickListener {
            val intent = Intent(this, ArsipAdminActivity::class.java)
            startActivity(intent)
        }

        urlClass = UrlClass()
    }

    override fun onStart() {
        super.onStart()
        showDataCuti("pending","")
    }

    private fun showDataCuti(mode: String, nama: String) {
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
                        frm.put("nama",jsonObject.getString("nama"))
                        frm.put("id_cuti",jsonObject.getString("id_cuti"))
                        frm.put("tanggal_cuti",jsonObject.getString("tanggal_cuti"))
                        frm.put("jenis_cuti",jsonObject.getString("jenis_cuti"))
                        frm.put("foto",jsonObject.getString("foto"))

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
                hm.put("nama",nama)
                when(mode){
                    "pending" -> {
                        hm.put("mode","pending")
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}