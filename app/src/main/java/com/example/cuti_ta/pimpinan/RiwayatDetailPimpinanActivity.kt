package com.example.cuti_ta.pimpinan

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.cuti_ta.R
import com.example.cuti_ta.UrlClass
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_cuti_detail.*
import org.json.JSONObject

class RiwayatDetailPimpinanActivity : AppCompatActivity() {

    lateinit var urlClass: UrlClass

    lateinit var preferences: SharedPreferences

    val PREF_NAME = "akun"
    val USERNAME = "username"
    val DEF_USERNAME = ""
    val PASSWORD = "password"
    val DEF_PASSWORD = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuti_detail)
        supportActionBar?.setTitle("Detail Cuti")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        urlClass = UrlClass()
        preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        btnLihatFotoDetail.setOnClickListener {
            evidence("evidence")
        }

        btnEditCuti.setOnClickListener {
            val editIntent = Intent(this, CutiEditPimpinanActivity::class.java)
            editIntent.putExtra("idCuti",tvDetailIdCutiAdmin.text.toString())
            startActivity(editIntent)
        }

        btnNextCuti.setOnClickListener {
            val editIntent = Intent(this, CutiValidasiActivity::class.java)
            editIntent.putExtra("idCuti", tvDetailIdCutiAdmin.text.toString())
            startActivity(editIntent)
        }

        btnHapusCutiAdmin.setText("Batalkan Cuti")
        btnHapusCutiAdmin.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Peringatan!")
                .setIcon(R.drawable.warning)
                .setMessage("Apakah Anda ingin membatalkan cuti ini?")
                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                    deleteCuti("batal")
                    Toast.makeText(this, "Berhasil membatalkan cuti!", Toast.LENGTH_SHORT).show()
                    onBackPressed()
                })
                .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->
                    null
                })
                .show()
            true
        }

        btnNextCuti.visibility = View.GONE
    }

    override fun onStart() {
        super.onStart()
        showDetailCuti("detail")
        showTtd("read_ttd_pimpinan")
        showTtdKetua("read_ttd_ketua")
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
                    btnEditCuti.visibility = View.VISIBLE
                } else {
                    val ttdTglP = jsonObject.getString("ttd_tglpimpinan")
                    val namaP = jsonObject.getString("namaP")
                    val nipP = jsonObject.getString("nipP")
                    val ttdP = jsonObject.getString("img_ttd_pimpinan")

                    btnEditCuti.visibility = View.GONE
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

                    btnEditCuti.visibility = View.GONE
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

                if (untKerja.equals("Panitera")) {
                    tvDetailCutiUnitKerja.setText("Kepaniteraan")
                } else {
                    tvDetailCutiUnitKerja.setText("Kesekretariatan")
                }
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

                if (statusCuti.equals("Pending")) {
                    tvDetailStatusCutiAdmin.setTextColor(Color.parseColor("#BAD6FF"))
                    tvDetailStatusCutiAdmin.setText(statusCuti)
                } else if (statusCuti.equals("Diterima")) {
                    tvDetailStatusCutiAdmin.setTextColor(Color.parseColor("#ABE8A1"))
                    tvDetailStatusCutiAdmin.setText(statusCuti)
                    btnHapusCutiAdmin.visibility = View.GONE
                } else {
                    tvDetailStatusCutiAdmin.setTextColor(Color.parseColor("#FF7C7C"))
                    tvDetailStatusCutiAdmin.setText(statusCuti)
                    btnHapusCutiAdmin.visibility = View.GONE
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
                        hm.put("id_cuti", paket?.getString("idCuti").toString())
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

    private fun deleteCuti(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlCuti,
            Response.Listener { response ->
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Tidak dapat terhubung ke server!", Toast.LENGTH_SHORT).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                when(mode){
                    "batal" -> {
                        hm.put("mode","batal")
                        hm.put("id_cuti", tvDetailIdCutiAdmin.text.toString())
                    }
                }

                return hm
            }
        }
        val  queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}