package com.example.cuti_ta.admin

import android.content.DialogInterface
import android.content.Intent
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
import com.example.cuti_ta.pimpinan.CutiValidasiActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_cuti_detail.*
import org.json.JSONObject

class CutiDetailAdminActivity : AppCompatActivity() {
    lateinit var urlClass: UrlClass

    var tvLama = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuti_detail)
        supportActionBar?.setTitle("Detail Cuti")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        urlClass = UrlClass()

        btnLihatFotoDetail.setOnClickListener {
            evidence("evidence")
        }

        btnEditCuti.setOnClickListener {
            val editIntent = Intent(this, CutiEditAdminActivity::class.java)
            editIntent.putExtra("idCuti",tvDetailIdCutiAdmin.text.toString())
//            editIntent.putExtra("nip",tvDetailNipCutiAdmin.text.toString())
            startActivity(editIntent)
        }

        btnNextCuti.setOnClickListener {
            var dialog = ValidasiDialogFragment()

            dialog.show(this.supportFragmentManager, "ValidasiDialogFragment")
        }

        btnHapusCutiAdmin.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Peringatan!")
                .setIcon(R.drawable.warning)
                .setMessage("Apakah Anda ingin Menghapus Cuti ini?")
                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                    deleteCuti("delete")
                    Toast.makeText(this, "Berhasil menghapus cuti!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, RiwayatAdminActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                })
                .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->
                    null
                })
                .show()
            true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onStart() {
        super.onStart()
        showDetailCuti("detail_admin")
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
                val untKerja = jsonObject.getString("unit_kerja")
                val alasanCuti = jsonObject.getString("alasan_cuti")
                val tanggalCuti = jsonObject.getString("tanggal_cuti")
                val selesaiCuti = jsonObject.getString("selesai_cuti")
                val lamaCuti = jsonObject.getString("lama_cuti")
                val alamatCuti = jsonObject.getString("alamat_cuti")
                val statusCuti = jsonObject.getString("status_cuti")
                val catatan = jsonObject.getString("catatan")

                tvDetailStatusCutiAdmin.setText(selesaiCuti)
                tvDetailIdCutiAdmin.setText(idCuti)
                tvDetailCutiNama.setText(nama)
                tvDetailCutiNip.setText(nip)
                tvDetailCutiJabatan.setText(jabatan)
                tvDetailCutiGolongan.setText(golongan)
                tvDetailCutiMasaKerja.setText(masaKerja+" Tahun")
                tvDetailCutiUnitKerja.setText(untKerja)
                tvDetailCutiJenis.setText(jenisCuti)
                tvDetailCutiAlasan.setText(alasanCuti)
                tvDetailCutiTglCuti.setText(tanggalCuti)
                tvDetailCutiLamaCuti.setText(lamaCuti+" Hari")
                tvDetailCutiSelesaiCuti.setText(selesaiCuti)
                tvDetailCutiAlamat.setText(alamatCuti)

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
                var paket : Bundle? = intent.extras
                when(mode){
                    "delete" -> {
                        hm.put("mode","delete")
                        hm.put("id_cutiizin",paket?.getString("idCuti").toString())
                        hm.put("status_trash",tvDetailStatusCutiAdmin.text.toString())
                        hm.put("nama",tvDetailCutiNama.text.toString())
                        hm.put("tgl_cutiizin",tvDetailCutiTglCuti.text.toString())
                    }
                }

                return hm
            }
        }
        val  queue = Volley.newRequestQueue(this)
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