package com.example.cuti_ta.admin

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
import com.example.cuti_ta.adapter.AdapterKuota
import kotlinx.android.synthetic.main.activity_kuota.*
import org.json.JSONArray

class KuotaAdminActivity : AppCompatActivity() {
    lateinit var urlClass: UrlClass

    val daftarSisa = mutableListOf<HashMap<String,String>>()
    lateinit var sisaAdapter: AdapterKuota

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kuota)
        supportActionBar?.setTitle("Kuota Cuti")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        urlClass = UrlClass()

        sisaAdapter = AdapterKuota(daftarSisa, this)
        rvSisa.layoutManager = LinearLayoutManager(this)
        rvSisa.adapter = sisaAdapter
    }

    override fun onStart() {
        super.onStart()
        showAllSisa("readAllKuota")
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun showAllSisa(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlSisa,
            Response.Listener { response ->
                daftarSisa.clear()
                if (response.equals(0)) {
                    Toast.makeText(this,"Data tidak ditemukan", Toast.LENGTH_LONG).show()
                } else {
                    val jsonArray = JSONArray(response)
                    for (x in 0..(jsonArray.length()-1)){
                        val jsonObject = jsonArray.getJSONObject(x)
                        var  sisa = HashMap<String,String>()
                        sisa.put("kd_kuota",jsonObject.getString("kd_kuota"))
                        sisa.put("nama",jsonObject.getString("nama"))
                        sisa.put("terpakai",jsonObject.getString("terpakai"))
                        sisa.put("foto",jsonObject.getString("foto"))
                        sisa.put("sisa_kuota",jsonObject.getString("sisa_kuota"))
                        sisa.put("tahun_kuota",jsonObject.getString("tahun_kuota"))

                        daftarSisa.add(sisa)
                    }
                    sisaAdapter.notifyDataSetChanged()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                when(mode){
                    "readAllKuota" -> {
                        hm.put("mode","readAllKuota")
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}