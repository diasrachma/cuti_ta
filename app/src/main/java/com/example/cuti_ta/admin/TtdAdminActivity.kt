package com.example.cuti_ta.admin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.cuti_ta.R
import com.example.cuti_ta.UrlClass
import com.example.cuti_ta.adapter.AdapterTtd
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_ttd.*
import org.json.JSONArray
import org.json.JSONObject

class TtdAdminActivity : AppCompatActivity() {
    lateinit var urlClass: UrlClass

    val daftarTtd = mutableListOf<HashMap<String, String>>()
    lateinit var ttdAdapter: AdapterTtd

    var idTtd = ""
    var jabatanPimpinan = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ttd)
        supportActionBar?.setTitle("Master TTD")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        urlClass = UrlClass()

        var paket : Bundle? = intent.extras
        jabatanPimpinan = paket?.getString("jabatan").toString()

        if (jabatanPimpinan.equals("Admin")) {
            cardViewTtd.visibility = View.GONE
        } else {
            cardViewTtd.visibility = View.VISIBLE
            showMyTtd("readMyTtd")
            btnInsertTtd.visibility = View.GONE
        }

        btnInsertTtd.setOnClickListener {
            val intent = Intent(this, TtdInsertAdminActiviity::class.java)
            startActivity(intent)
        }

        ttdAdapter = AdapterTtd(daftarTtd, this)
        rvTtd.layoutManager = LinearLayoutManager(this)
        rvTtd.adapter = ttdAdapter
    }

    override fun onStart() {
        super.onStart()
        showAllTtd("readAllTtd")
    }

    var namaPimpinan = ""
    private fun showMyTtd(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlTtd,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val nama = jsonObject.getString("nama")
                val nip = jsonObject.getString("nip")
                val foto = jsonObject.getString("img_ttd")

                Picasso.get().load(foto).into(ttdAnda)
                ttdAndaNama.setText("Nama : "+nama)
                ttdAndaNip.setText("NIP : "+nip)
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                var paket : Bundle? = intent.extras
                namaPimpinan = paket?.getString("nama").toString()
                when(mode){
                    "readMyTtd" -> {
                        hm.put("mode","readMyTtd")
                        hm.put("nama", namaPimpinan)
                    }
                }

                return hm
            }
        }
        val  queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun deleteTtd(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlTtd,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val respon = jsonObject.getString("respon")

            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                when(mode){
                    "deleteTtd" -> {
                        hm.put("mode","deleteTtd")
                        hm.put("id_ttd", idTtd)
                    }
                }

                return hm
            }
        }
        val  queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    private fun showAllTtd(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlTtd,
            Response.Listener { response ->
                daftarTtd.clear()
                if (response.equals(0)) {
                    Toast.makeText(this,"Data tidak ditemukan", Toast.LENGTH_LONG).show()
                } else {
                    val jsonArray = JSONArray(response)
                    for (x in 0..(jsonArray.length()-1)){
                        val jsonObject = jsonArray.getJSONObject(x)
                        var  frm = HashMap<String,String>()
                        frm.put("id_ttd",jsonObject.getString("id_ttd"))
                        frm.put("nama",jsonObject.getString("nama"))
                        frm.put("jabatan",jsonObject.getString("jabatan"))
                        frm.put("nip",jsonObject.getString("nip"))
                        frm.put("img_ttd",jsonObject.getString("img_ttd"))

                        daftarTtd.add(frm)
                    }
                    ttdAdapter.notifyDataSetChanged()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                when(mode){
                    "readAllTtd" -> {
                        hm.put("mode","readAllTtd")
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}