package com.example.cuti_ta.pimpinan

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.cuti_ta.R
import com.example.cuti_ta.UrlClass
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_cuti_detail.*
import org.json.JSONObject

class ArsipDetailPimpinanActivity : AppCompatActivity() {

    lateinit var urlClass: UrlClass
    lateinit var preferences: SharedPreferences

    val PREF_NAME = "akun"
    val JABATAN = "jabatan"
    val DEF_JABATAN = ""

    var jbt = ""
    var sts = ""

    var tvLama = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuti_detail)
        supportActionBar?.setTitle("Detail Cuti")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        urlClass = UrlClass()
        preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        if (jbt == preferences.getString(JABATAN, DEF_JABATAN).toString()) {
            if (sts.equals("Pending")) {
                btnNextCuti.visibility = View.GONE
            }
        }

        btnLihatFotoDetail.setOnClickListener {
            evidence("evidence")
        }

        btnNextCuti.setOnClickListener {
            val intent = Intent(this, CutiValidasiActivity::class.java)
            intent.putExtra("idCuti", tvDetailIdCutiAdmin.text.toString())
            intent.putExtra("nip", tvDetailCutiNip.text.toString())
            intent.putExtra("lama", tvLama)
            startActivity(intent)
        }

        btnEditCuti.visibility = View.GONE
        btnHapusCutiAdmin.visibility = View.GONE

        refresh.setOnRefreshListener {
            showDetailCuti("detail_admin")
            showTtd("read_ttd_pimpinan")
            showTtdKetua("read_ttd_ketua")

            // Hentikan animasi refresh setelah selesai
            refresh.isRefreshing = false
            refreshData()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onStart() {
        super.onStart()
        showTtd("read_ttd_pimpinan")
        showTtdKetua("read_ttd_ketua")
        showDetailCuti("detail_admin")
    }

    fun refreshData() {
        Handler().postDelayed({
            recreate()
            showDetailCuti("detail_admin")
            showTtd("read_ttd_pimpinan")
            showTtdKetua("read_ttd_ketua")

            refresh.isRefreshing = false
        }, 2000)
    }

    fun showTtd(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlTtd,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val respons = jsonObject.getString("respon")
                if (respons.equals("0")) {
                    tvNamaTtd1.visibility = View.GONE
                    tvNipTtd1.visibility = View.GONE
                    imgTtd1.visibility = View.GONE
                } else {
                    val ttdTglP = jsonObject.getString("ttd_tglpimpinan")
                    val namaP = jsonObject.getString("namaP")
                    val nipP = jsonObject.getString("nipP")
                    val ttdP = jsonObject.getString("img_ttd_pimpinan")

                    tvTglTtd1.setText("Kediri, "+ttdTglP)
                    tvNamaTtd1.setText(namaP)
                    tvNipTtd1.setText(nipP)
                    Picasso.get().load(ttdP).into(imgTtd1)
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Tidak dapat terhubung ke server!", Toast.LENGTH_SHORT).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                var paket : Bundle? = intent.extras
                when(mode){
                    "read_ttd_pimpinan" -> {
                        hm.put("mode","read_ttd_pimpinan")
                        hm.put("id_cuti", paket?.getString("idCuti").toString())
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun showTtdKetua(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlTtd,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val respons = jsonObject.getString("respon")
                if (respons.equals("0")) {
                    tvNamaTtd2.visibility = View.GONE
                    tvNipTtd2.visibility = View.GONE
                    imgTtd2.visibility = View.GONE
                } else {
                    val ttdTglK = jsonObject.getString("ttd_tglketua")
                    val namaK = jsonObject.getString("namaK")
                    val nipK = jsonObject.getString("nipK")
                    val ttdK = jsonObject.getString("img_ttd_ketua")

                    tvTglTtd2.setText("Kediri, "+ttdTglK)
                    tvNamaTtd2.setText(namaK)
                    tvNipTtd2.setText(nipK)
                    Picasso.get().load(ttdK).into(imgTtd2)
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Tidak dapat terhubung ke server!", Toast.LENGTH_SHORT).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                var paket : Bundle? = intent.extras
                when(mode){
                    "read_ttd_ketua" -> {
                        hm.put("mode","read_ttd_ketua")
                        hm.put("id_cuti", paket?.getString("idCuti").toString())
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun showDetailCuti(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlCuti,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val nama = jsonObject.getString("nama")
                val nip = jsonObject.getString("nip")
                val jabatan = jsonObject.getString("jabatan")
                val golongan = jsonObject.getString("golongan")
                val masaKerja = jsonObject.getString("masa_kerja")
                val idCuti = jsonObject.getString("id_cuti")
                val jenisCuti = jsonObject.getString("jenis_cuti")
                val alasanCuti = jsonObject.getString("alasan_cuti")
                val tanggalCuti = jsonObject.getString("tanggal_cuti")
                val untKerja = jsonObject.getString("unit_kerja")
                val selesaiCuti = jsonObject.getString("selesai_cuti")
                val lamaCuti = jsonObject.getString("lama_cuti")
                val alamatCuti = jsonObject.getString("alamat_cuti")
                val statusCuti = jsonObject.getString("status_cuti")
                val catatan = jsonObject.getString("catatan")

                tvDetailCutiUnitKerja.setText(untKerja)
                tvDetailStatusCutiAdmin.setText(selesaiCuti)
                tvDetailIdCutiAdmin.setText(idCuti)
                tvDetailCutiNama.setText(nama)
                tvDetailCutiNip.setText(nip)
                tvDetailCutiJabatan.setText(jabatan)
                tvDetailCutiGolongan.setText(golongan)
                tvDetailCutiMasaKerja.setText(masaKerja+" Tahun")
                tvDetailCutiJenis.setText(jenisCuti)
                tvDetailCutiAlasan.setText(alasanCuti)
                tvDetailCutiTglCuti.setText(tanggalCuti)
                tvDetailCutiLamaCuti.setText(lamaCuti+" Hari")
                tvDetailCutiSelesaiCuti.setText(selesaiCuti)
                tvDetailCutiAlamat.setText(alamatCuti)

                jbt = jabatan
                sts = statusCuti

                tvLama = lamaCuti

                if (catatan.equals("null")){
                    tv.visibility = View.GONE
                    tvCatatanCutiAdmin.visibility = View.GONE
                } else {
                    tvCatatanCutiAdmin.setText(catatan)
                }

                if (jenisCuti.equals("Cuti Tahunan")) {
                    btnLihatFotoDetail.visibility = View.GONE
                } else {
                    btnLihatFotoDetail.visibility = View.VISIBLE
                }

                if (jabatan.equals("Panitera") || jabatan.equals("Sekretaris")) {
                    if (statusCuti.equals("Diterima")) {
                        tvTglTtd1.visibility = View.GONE
                    }
                }

                if (statusCuti.equals("Pending")) {
                    tvDetailStatusCutiAdmin.setTextColor(Color.parseColor("#BAD6FF"))
                    tvDetailStatusCutiAdmin.setText(statusCuti)
                    btnNextCuti.visibility = View.VISIBLE
                } else if (statusCuti.equals("Pending1")) {
                    tvDetailStatusCutiAdmin.setTextColor(Color.parseColor("#FF9800"))
                    tvDetailStatusCutiAdmin.text = "Acc Pimpinan"
                    btnNextCuti.visibility = View.VISIBLE
                } else if (statusCuti.equals("Diterima")) {
                    tvDetailStatusCutiAdmin.setTextColor(Color.parseColor("#ABE8A1"))
                    tvDetailStatusCutiAdmin.setText(statusCuti)
                    btnNextCuti.visibility = View.GONE
                } else {
                    tvDetailStatusCutiAdmin.setTextColor(Color.parseColor("#FF7C7C"))
                    tvDetailStatusCutiAdmin.setText(statusCuti)
                    btnNextCuti.visibility = View.GONE
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Tidak dapat terhubung ke server!", Toast.LENGTH_SHORT).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                var paket : Bundle? = intent.extras
                when(mode){
                    "detail_admin" -> {
                        hm.put("mode","detail_admin")
                        hm.put("id_cuti", paket?.getString("idCuti").toString())
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun evidence(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlCuti,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val fotoBukti = jsonObject.getString("evidence")

                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(fotoBukti)
                startActivity(intent)
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Tidak dapat terhubung ke server!", Toast.LENGTH_SHORT).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                var paket : Bundle? = intent.extras
                hm.put("id_cuti",paket?.getString("idCuti").toString())
                when(mode){
                    "evidence" -> {
                        hm.put("mode","evidence")
                    }
                }

                return hm
            }
        }
        val  queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}