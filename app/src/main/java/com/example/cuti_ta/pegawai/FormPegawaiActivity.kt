package com.example.cuti_ta.pegawai

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.apk_pn.Helper.MediaHelper
import com.example.cuti_ta.BaseApplication
import com.example.cuti_ta.R
import com.example.cuti_ta.UrlClass
import kotlinx.android.synthetic.main.activity_form.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class FormPegawaiActivity : AppCompatActivity() {
    lateinit var urlClass: UrlClass
    lateinit var preferences: SharedPreferences

    val PREF_NAME = "akun"
    val USERNAME = "username"
    val DEF_USERNAME = ""
    val PASSWORD = "password"
    val DEF_PASSWORD = ""

    var tahun = 0
    var bulan = 0
    var hari = 0

    val jenisSpinner = arrayOf("Cuti Tahunan", "Cuti Besar", "Cuti Sakit", "Cuti Melahirkan", "Cuti Karena Alasan Penting", "Cuti di Luar Tanggungan Negara")

    lateinit var adapterJenis: ArrayAdapter<String>
    var pilihan = ""

    lateinit var mediaHealper: MediaHelper
    var imStr = ""

    var kuota = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)
        supportActionBar?.hide()
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        mediaHealper = MediaHelper(this)
        urlClass = UrlClass()

        preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        tvNamaCuti.setEnabled(false)

        adapterJenis = ArrayAdapter(this, android.R.layout.simple_list_item_1,jenisSpinner)

        val kalender : Calendar = Calendar.getInstance()

        tahun = kalender.get(Calendar.YEAR)
        bulan = kalender.get(Calendar.MONTH) + 1
        hari = kalender.get(Calendar.DAY_OF_MONTH)

        spJenisCuti.adapter = adapterJenis

        spJenisCuti.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent : AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    pilihan = "Cuti Tahunan"
                    boxFotoSakit.visibility = View.GONE
//                    Toast.makeText(baseContext,adapterJenis.getItem(position),Toast.LENGTH_SHORT).show()
                } else {
                    boxFotoSakit.visibility = View.VISIBLE
//                    Toast.makeText(baseContext,adapterJenis.getItem(position),Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnTglCuti.setOnClickListener {
            showDialog(10)
        }

        btnSelesaiCuti.setOnClickListener {
            showDialog(20)
        }

        btnSimpanFormCuti.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Kirim Cuti")
                .setIcon(R.drawable.warning)
                .setMessage("Apakah anda yakin ingin mengirim form permohonan cuti?")
                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                    if (spJenisCuti.selectedItem.toString().equals("Cuti Tahunan")) {
                        if (txLamaCuti.text.toString() >= kuota) {
                            AlertDialog.Builder(this)
                                .setTitle("Peringatan!")
                                .setIcon(R.drawable.warning)
                                .setMessage("Gagal mengajukan cuti, kuota Anda tidak mencukupi!")
                                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                                    null
                                })
                                .show()
                        } else {
                            addDataCuti("insert")
                        }
                    } else {
                        addDataCuti("insert_with_photo")
                    }
                })
                .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->

                })
                .show()
        }

        btnChooseFile.setOnClickListener {
            val intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(intent,mediaHealper.RcGallery())
        }

        btnCancelFormCuti.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Batal Cuti")
                .setMessage("Apakah anda yakin ingin membatalkan?")
                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                    onBackPressed()
                })
                .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->

                })
                .show()
        }
    }

    override fun onStart() {
        super.onStart()
        showProfil("show_profil")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == mediaHealper.RcGallery()){
                imStr = mediaHealper.getBitmapToString(data!!.data,imgEvidenceSakit)
            }
        }
    }

    var ubahTanggalDialog = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
        tvTanggalCuti.text = "$year-${month+1}-$dayOfMonth"
        tahun = year
        bulan = month+1
        hari = dayOfMonth
    }

    var selesaiCutiDialog = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
        tvSelesaiCuti.text = "$year-${month+1}-$dayOfMonth"
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

    private fun showProfil(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlProfil,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val nama = jsonObject.getString("nama")
                val nip = jsonObject.getString("nip")
                val jabatan = jsonObject.getString("jabatan")
                val golongan = jsonObject.getString("golongan")
                val telpon = jsonObject.getString("no_hp")
                val sisa = jsonObject.getString("sisa_kuota")

                kuota = sisa
                tvNamaCuti.setText(nama)
                tvNipCuti.setText(nip)
                tvJabatanCuti.setText(jabatan)
                tvGolonganCuti.setText(golongan)
                tvNohpCuti.setText(telpon)
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = java.util.HashMap<String, String>()
                when(mode){
                    "show_profil" -> {
                        hm.put("mode","form_profil")
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

    fun addDataCuti(mode : String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlCuti,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val respon = jsonObject.getString("respon")

                if (respon.equals("1")) {
                    Toast.makeText(this, "Berhasil Mengajukan Permohonan Cuti", Toast.LENGTH_LONG)
                        .show()
                    BaseApplication.notificationHelper.showCutiNotif(
                        tvNamaCuti.text.toString(),
                        "Mengajukan ${spJenisCuti.selectedItem} pada Tanggal ${tvTanggalCuti.text}."
                    )
                } else {
                    Toast.makeText(this, "Gagal Mengajukan Permohonan Cuti", Toast.LENGTH_LONG)
                        .show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String, String>()
                val nmFile ="SAKIT"+ SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(
                    Date()
                )+".jpg"
                when(mode){
                    "insert"->{
                        hm.put("mode","insert")
                        hm.put("nip", tvNipCuti.text.toString())
                        hm.put("jenis_cuti", spJenisCuti.selectedItem.toString())
                        hm.put("alasan_cuti", tvAlasanCuti.text.toString())
                        hm.put("tanggal_cuti", tvTanggalCuti.text.toString())
                        hm.put("selesai_cuti", tvSelesaiCuti.text.toString())
                        hm.put("lama_cuti", txLamaCuti.text.toString())
                        hm.put("alamat_cuti", tvAlamatCuti.text.toString())
                    }
                    "insert_with_photo"->{
                        hm.put("mode","insert_with_photo")
                        hm.put("nip", tvNipCuti.text.toString())
                        hm.put("jenis_cuti", spJenisCuti.selectedItem.toString())
                        hm.put("alasan_cuti", tvAlasanCuti.text.toString())
                        hm.put("tanggal_cuti", tvTanggalCuti.text.toString())
                        hm.put("selesai_cuti", tvSelesaiCuti.text.toString())
                        hm.put("lama_cuti", txLamaCuti.text.toString())
                        hm.put("alamat_cuti", tvAlamatCuti.text.toString())
                        hm.put("image",imStr)
                        hm.put("file",nmFile)
                    }
                }
                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}