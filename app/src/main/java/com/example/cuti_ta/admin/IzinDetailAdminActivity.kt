package com.example.cuti_ta.admin

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.cuti_ta.R
import com.example.cuti_ta.UrlClass
import kotlinx.android.synthetic.main.activity_izin_detail.*
import org.json.JSONObject

class IzinDetailAdminActivity : AppCompatActivity() {
    lateinit var urlClass: UrlClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_izin_detail)
        supportActionBar?.setTitle("Detail Izin Keluar")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        urlClass = UrlClass()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onStart() {
        super.onStart()
        detailIzin("detail")
        btnKembaliIzin.visibility = View.GONE
    }

    fun detailIzin(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlIzin,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val nama = jsonObject.getString("nama")
                val nip = jsonObject.getString("nip")
                val jabatan = jsonObject.getString("jabatan")
                val idIzin = jsonObject.getString("id_izinkeluar")
                val jam = jsonObject.getString("jam_izinkeluar")
                val alasan = jsonObject.getString("alasan_izinkeluar")
                val tanggal = jsonObject.getString("tgl_izinkeluar")
                val alamat = jsonObject.getString("alamat_izinkeluar")
                val status = jsonObject.getString("status_izinkeluar")
                val jamkembali = jsonObject.getString("jam_kembali")
                val telat = jsonObject.getString("telat_kembali")

                detailNamaIzin.setText(nama)
                detailNipIzin.setText(nip)
                detailJabatanIzin.setText(jabatan)
                tvDetailIdIzin.setText(idIzin)
                detailJamIzin.setText(jam)
                detailKembaliIzin.setText(jamkembali)
                detailTanggalIzin.setText(tanggal)
                detailAlamatIzin.setText(alamat)
                detailAlasanIzin.setText(alasan)

                if (status.equals("Selesai")) {
                    if (telat.equals("00:00:00")) {
                        detailTerlambat.setText("Tidak terlambat")
                        detailTerlambat.setTextColor(Color.BLUE)
                    } else {
                        detailTerlambat.setText(telat)
                    }
                    tvDetailStatusIzin.setText(status)
                    tvDetailStatusIzin.setTextColor(Color.parseColor("#03C988"))
                    btnKembaliIzin.visibility = View.GONE
                } else {
                    detailTerlambat.visibility = View.GONE
                    tvDetailStatusIzin.setText(status)
                    tvDetailStatusIzin.setTextColor(Color.parseColor("#1D267D"))
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Tidak dapat terhubung ke server!", Toast.LENGTH_SHORT).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                var paket : Bundle? = intent.extras
                when(mode){
                    "detail" -> {
                        hm.put("mode","detail")
                        hm.put("id_izinkeluar", paket?.getString("idIzin").toString())
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}