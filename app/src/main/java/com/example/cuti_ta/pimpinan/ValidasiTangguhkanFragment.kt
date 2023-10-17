package com.example.cuti_ta.pimpinan

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.cuti_ta.R
import com.example.cuti_ta.UrlClass
import com.example.cuti_ta.admin.CutiDetailAdminActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialog_ditangguhkan.view.*
import org.json.JSONObject

class ValidasiTangguhkanFragment : DialogFragment() {

    lateinit var preferences: SharedPreferences
    val PREF_NAME = "akun"
    val USERNAME = "username"
    val DEF_USERNAME = ""
    val PASSWORD = "password"
    val DEF_PASSWORD = ""

    lateinit var urlClass: UrlClass
    lateinit var v : View
    var idC = ""
    var lvl = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = layoutInflater.inflate(R.layout.dialog_ditangguhkan, container, false)

        idC = arguments?.getString("idCuti").toString()

        urlClass = UrlClass()
        preferences = v.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        v.btnKirimTangguhkan.setOnClickListener {
            AlertDialog.Builder(v.context)
                .setIcon(R.drawable.warning)
                .setTitle("Informasi!")
                .setMessage("Apakah Anda yakin ingin menangguhkan Cuti ini?")
                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                    if (!v.txCatatanValidasiDitangguhkan.text.toString().equals("")) {
                        tangguhkan("tangguhkan")
                        if (lvl.equals("Admin")) {
                            val intent = Intent(v.context, CutiDetailAdminActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            intent.putExtra("idCuti", idC)
                            startActivity(intent)
                        } else {
                            val intent = Intent(v.context, ArsipDetailPimpinanActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            intent.putExtra("idCuti", idC)
                            startActivity(intent)
                        }
                    } else {
                        Toast.makeText(v.context, "Catatan tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                    }
                })
                .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->
                    Toast.makeText(v.context, "Berhasil membatalkan menangguhkan Cuti!", Toast.LENGTH_SHORT).show()
                })
                .show()
            true
        }

        return v
    }

    override fun onStart() {
        super.onStart()
        showProfil("show_profil")
    }

    fun tangguhkan(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlCuti,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val respon = jsonObject.getString("respon")

                if(respon.equals("0")){
                    Toast.makeText(this.context,"Gagal Melakukan Validasi", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(v.context, "Berhasil menangguhkan Cuti!", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this.context,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                idC = arguments?.getString("idCuti").toString()
                when(mode){
                    "tangguhkan" -> {
                        hm.put("mode","tangguhkan")
                        hm.put("id_cuti", idC)
                        hm.put("catatan", v.txCatatanValidasiDitangguhkan.text.toString())
                    }
                }

                return hm
            }
        }
        val  queue = Volley.newRequestQueue(this.context)
        queue.add(request)
    }

    private fun showProfil(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlProfil,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val level = jsonObject.getString("level")

                lvl = level
            },
            Response.ErrorListener { error ->
                Toast.makeText(this.context,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                when(mode){
                    "show_profil" -> {
                        hm.put("mode","show_profil")
                        hm.put("username",preferences.getString(USERNAME, DEF_USERNAME).toString())
                        hm.put("password",preferences.getString(PASSWORD, DEF_PASSWORD).toString())
                    }
                }
                return hm
            }
        }
        val queue = Volley.newRequestQueue(this.context)
        queue.add(request)
    }
}