package com.example.cuti_ta.admin

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.cuti_ta.R
import com.example.cuti_ta.UrlClass
import kotlinx.android.synthetic.main.activity_cuti_edit.*
import org.json.JSONObject
import java.util.*

class CutiEditAdminActivity : AppCompatActivity() {

    lateinit var urlClass: UrlClass

    var tahun = 0
    var bulan = 0
    var hari = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuti_edit)
        supportActionBar?.setTitle("Edit Cuti")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        urlClass = UrlClass()

        var paket : Bundle? = intent.extras
        edtNipCuti.setText(paket?.getString("nip"))

        val kalender : Calendar = Calendar.getInstance()

        tahun = kalender.get(Calendar.YEAR)
        bulan = kalender.get(Calendar.MONTH) + 1
        hari = kalender.get(Calendar.DAY_OF_MONTH)

        btnEditCuti.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Kirim Cuti")
                .setMessage("Apakah anda yakin ingin mengirim form permohonan cuti?")
                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                    updateDataCuti("update")
                    onBackPressed()
                })
                .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->

                })
                .show()
        }

        btnEdtTglCuti.setOnClickListener {
            showDialog(10)
        }

        btnEdtSelesaiCuti.setOnClickListener {
            showDialog(20)
        }

    }

    var ubahTanggalDialog = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
        edtTanggalCuti.text = "$year-${month+1}-$dayOfMonth"
        tahun = year
        bulan = month+1
        hari = dayOfMonth
    }

    var selesaiCutiDialog = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
        edtSelesaiCuti.text = "$year-${month+1}-$dayOfMonth"
        tahun = year
        bulan = month+1
        hari = dayOfMonth
    }

    override fun onCreateDialog(id: Int): Dialog {
        when(id){
            10 -> return DatePickerDialog(this, ubahTanggalDialog, tahun, bulan-1, hari)
            20 -> return DatePickerDialog(this, selesaiCutiDialog, tahun, bulan-1, hari)
        }
        return super.onCreateDialog(id)
    }

    fun updateDataCuti(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlCuti,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val respon = jsonObject.getString("respon")
//
                if (respon.equals("1")) {
                    Toast.makeText(this, "Berhasil Mengedit Form Permohonan Cuti", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(this, "Gagal Mengedit Form Permohonan Cuti", Toast.LENGTH_LONG)
                        .show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String, String>()
                when(mode){
                    "update"->{
                        hm.put("mode","update")
                        hm.put("id_cuti", edtIdCuti.text.toString())
                        hm.put("alasan_cuti", edtAlasanCuti.text.toString())
                        hm.put("tanggal_cuti", edtTanggalCuti.text.toString())
                        hm.put("selesai_cuti", edtSelesaiCuti.text.toString())
                        hm.put("lama_cuti", edtLamaCuti.text.toString())
                        hm.put("alamat_cuti", edtAlamatCuti.text.toString())
                    }
                }
                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onStart() {
        super.onStart()
        showEditCuti("detail_admin")
    }

    fun showEditCuti(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlCuti,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val idCuti = jsonObject.getString("id_cuti")
                val alasanCuti = jsonObject.getString("alasan_cuti")
                val tanggalCuti = jsonObject.getString("tanggal_cuti")
                val selesaiCuti = jsonObject.getString("selesai_cuti")
                val lamaCuti = jsonObject.getString("lama_cuti")
                val alamatCuti = jsonObject.getString("alamat_cuti")
                val statusCuti = jsonObject.getString("status_cuti")

                val nama = jsonObject.getString("nama")
                val nip = jsonObject.getString("nip")
                val jabatan = jsonObject.getString("jabatan")
                val golongan = jsonObject.getString("golongan")
                val nohp = jsonObject.getString("no_hp")

                edtNamaCuti.setText(nama)
                edtNipCuti.setText(nip)
                edtJabatanCuti.setText(jabatan)
                edtGolonganCuti.setText(golongan)
                edtNohpCuti.setText(nohp)

                edtIdCuti.setText(idCuti)
                edtAlasanCuti.setText(alasanCuti)
                edtTanggalCuti.setText(tanggalCuti)
                edtSelesaiCuti.setText(selesaiCuti)
                edtLamaCuti.setText(lamaCuti)
                edtAlamatCuti.setText(alamatCuti)
                tvEditStatusCuti.setText(statusCuti)
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                var paket : Bundle? = intent.extras
                hm.put("id_cuti", paket?.getString("idCuti").toString())
                when(mode){
                    "detail_admin" -> {
                        hm.put("mode","detail_admin")
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}