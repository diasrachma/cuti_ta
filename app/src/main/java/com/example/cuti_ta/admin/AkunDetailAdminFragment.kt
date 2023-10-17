package com.example.cuti_ta.admin

import android.content.DialogInterface
import android.content.Intent
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
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_akun_detail.*
import kotlinx.android.synthetic.main.fragment_akun_detail.view.*
import org.json.JSONObject
import java.util.HashMap

class AkunDetailAdminFragment : DialogFragment() {
    lateinit var thisParent : AkunAdminActivity
    lateinit var urlClass: UrlClass
    lateinit var v : View
    var namaText = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        thisParent = activity as AkunAdminActivity
        urlClass = UrlClass()
        v = inflater.inflate(R.layout.fragment_akun_detail, container, false)

        val data = arguments
        v.tvUsernameAkunDetail.setText(data?.get("username").toString())

        v.btnClose.setOnClickListener {
            dismiss()
        }

        v.btnAktifAkunDetail.setOnClickListener {
            AlertDialog.Builder(thisParent)
                .setTitle("Info!")
                .setIcon(R.drawable.warning)
                .setMessage("Akun ini sekarang dalam keadaan nonaktif, Apakah Anda ingin mengaktifkan akun?")
                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                    Aktif("aktif")
                    dismiss()
                    thisParent.recreate()
                    Toast.makeText(thisParent,"Akun "+ namaText +" telah diaktifkan", Toast.LENGTH_LONG).show()
                })
                .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->

                })
                .show()
        }

        v.btnNonaktifAkunDetail.setOnClickListener {
            AlertDialog.Builder(thisParent)
                .setTitle("Info!")
                .setIcon(R.drawable.warning)
                .setMessage("Apakah Anda ingin menonaktifkan akun?")
                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                    nonAktif("non_aktif")
                    dismiss()
                    thisParent.recreate()
                    Toast.makeText(thisParent,"Akun "+ namaText +" telah dinonaktifkan", Toast.LENGTH_LONG).show()
                })
                .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->

                })
                .show()
        }

        v.btnEditAkunDetail.setOnClickListener {
            val intent = Intent(it.context, AkunEditActivity::class.java)
            intent.putExtra("username", arguments?.get("username").toString())
            it.context.startActivity(intent)
            dismiss()
        }

        return v
    }

    override fun onStart() {
        super.onStart()
        showDetailUser("detailAkun")
    }

    private fun Aktif(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlAkun,
            Response.Listener { response ->
                var prof = HashMap<String,String>()
                val data = arguments
                prof.put("username",data?.get("username").toString())
            },
            Response.ErrorListener { error ->
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                val data = arguments
                hm.put("username",data?.get("username").toString())
                when(mode){
                    "aktif" -> {
                        hm.put("mode","aktif")
                    }
                }
                return hm
            }
        }
        val queue = Volley.newRequestQueue(thisParent)
        queue.add(request)
    }

    private fun nonAktif(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlAkun,
            Response.Listener { response ->
                var prof = HashMap<String,String>()
                val data = arguments
                prof.put("username",data?.get("username").toString())
            },
            Response.ErrorListener { error ->
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                val data = arguments
                hm.put("username",data?.get("username").toString())
                when(mode){
                    "non_aktif" -> {
                        hm.put("mode","non_aktif")
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(thisParent)
        queue.add(request)
    }

    private fun showDetailUser(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlAkun,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                var prof = HashMap<String,String>()
                val data = arguments
                val nama = jsonObject.getString("nama")
                val nip = jsonObject.getString("nip")
                val passwordName = jsonObject.getString("password")
                val level = jsonObject.getString("level")
                val sts = jsonObject.getString("sts_akun")
                namaText = nama

                if (sts.equals("AKTIF")) {
                    btnAktifAkunDetail.visibility = View.GONE
                    btnNonaktifAkunDetail.visibility = View.VISIBLE
                } else {
                    btnAktifAkunDetail.visibility = View.VISIBLE
                    btnNonaktifAkunDetail.visibility = View.GONE
                }

                prof.put("username",data?.get("username").toString())

                tvNamaAkunDetail.setText(nama)
                tvNipAkunDetail.setText(nip)
                tvPasswordAkunDetail.setText(passwordName)
                tvLevelAkunDetail.setText(level)
            },
            Response.ErrorListener { error ->
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                val data = arguments
                hm.put("username",data?.get("username").toString())
                when(mode){
                    "detailAkun" -> {
                        hm.put("mode","detailAkun")
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(thisParent)
        queue.add(request)
    }
}