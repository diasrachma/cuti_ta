package com.example.cuti_ta.admin

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.apk_pn.Helper.MediaHelper
import com.example.cuti_ta.R
import com.example.cuti_ta.UrlClass
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_user_edit.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class UserEditAdminActivity : AppCompatActivity() {

    lateinit var urlClass: UrlClass
    lateinit var mediaHealper: MediaHelper
    var imStr = ""

    var unitK = ""

    lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_edit)
        supportActionBar?.hide()
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        urlClass = UrlClass()

        mediaHealper = MediaHelper(this)

        var paket : Bundle? = intent.extras
        edtNipUser.setText(paket?.getString("nip").toString())

        btnEditImageProfil.setOnClickListener {
            edtImageUser.visibility = View.GONE
            edtImageUserUpdate.visibility = View.VISIBLE
            val intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(intent,mediaHealper.RcGallery())

//            var alertBuilder = AlertDialog.Builder(this)
//            alertBuilder.setTitle("Informasi").setMessage("Jika ingin mengubah foto profil tetapi di layar fotonya tidak berubah jangan khawatir. Tekan tombol Simpan saja maka foto akan otomatis dirubah oleh sistem")
//            alertBuilder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
//            })
//            alertBuilder.show()
        }

        btnSimpanEditUser.setOnClickListener {
            AlertDialog.Builder(this)
                .setIcon(R.drawable.warning)
                .setTitle("Informasi!").setMessage("Yakin ingin merubah data profil pada User?")
            .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                Toast.makeText(this,"Berhasil merubah profil "+edtNamaUser.text.toString(), Toast.LENGTH_LONG).show()
                updateDataProfil("updateUser")
                onBackPressed()
                recreate()
            })
            .setNegativeButton("Batal",null)
            .show()
            true
        }
    }

    override fun onStart() {
        super.onStart()
        showProfil("detailUser")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == mediaHealper.RcGallery()){
                imStr = mediaHealper.getBitmapToString(data!!.data,edtImageUserUpdate)
            }
        }
    }

    private fun showProfil(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlUser,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val nama = jsonObject.getString("nama")
                val jabatan = jsonObject.getString("jabatan")
                val golongan = jsonObject.getString("golongan")
                val telpon = jsonObject.getString("no_hp")
                val unitKerja = jsonObject.getString("unit_kerja")
                val masaKerja = jsonObject.getString("masa_kerja")
                val foto = jsonObject.getString("foto")

                if (jabatan.equals("null") || golongan.equals("null") || telpon.equals("null") || masaKerja.equals("null")) {
                    edtNamaUser.setText(nama)
                    edtJabatanUser.setText("")
                    edtGolonganUser.setText("")
                    edtNoHpUser.setText("")
                    edtMasaKerjaUser.setText("")
                } else {
                    edtNamaUser.setText(nama)
                    edtJabatanUser.setText(jabatan)
                    edtGolonganUser.setText(golongan)
                    edtNoHpUser.setText(telpon)
                    edtMasaKerjaUser.setText(masaKerja)
                }

                edtUnitKerjaUser.setText(unitKerja)
                if (foto.equals("null")){
                    Picasso.get().load(R.drawable.akun).into(edtImageUser)
                } else {
                    Picasso.get().load(foto).into(edtImageUser)
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                var paket : Bundle? = intent.extras
                hm.put("nip", paket?.getString("nip").toString())
                when(mode) {
                    "detailUser" -> {
                        hm.put("mode", "detailUser")
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    private fun updateDataProfil(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlUser,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val respon = jsonObject.getString("respon")

                if(respon.equals("1")){
//                    Toast.makeText(this,"Berhasil merubah profil"+edtNamaProfil.text.toString(),Toast.LENGTH_LONG).show()
                }else{
//                    Toast.makeText(this,"Gagal merubah profil",Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                val nmFile ="IMG-"+ SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(
                    Date()
                )+".jpg"
                hm.put("nip",edtNipUser.text.toString())
                hm.put("nama",edtNamaUser.text.toString())
                hm.put("jabatan",edtJabatanUser.text.toString())
                hm.put("golongan",edtGolonganUser.text.toString())
                hm.put("unit_kerja", edtUnitKerjaUser.text.toString())
                hm.put("masa_kerja",edtMasaKerjaUser.text.toString())
                hm.put("no_hp",edtNoHpUser.text.toString())
                hm.put("image",imStr)
                hm.put("file",nmFile)

                when(mode) {
                    "updateUser" -> {
                        hm.put("mode", "updateUser")
                    }
                }

                return hm
            }
        }
        val  queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}