package com.example.cuti_ta.admin

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.cuti_ta.R
import com.example.cuti_ta.UrlClass
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_user_detail.view.*
import org.json.JSONObject
import java.util.HashMap

class UserDetailAdminFragment : DialogFragment() {

    lateinit var thisParent : UserAdminActivity
    lateinit var v : View
    lateinit var urlClass: UrlClass

    var terimaJabatan = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        thisParent = activity as UserAdminActivity
        urlClass = UrlClass()
        v = inflater.inflate(R.layout.fragment_user_detail, container, false)

        v.btnCloseUserDetail.setOnClickListener {
            dismiss()
        }

        v.btnEditUserDetail.setOnClickListener {
            val intent = Intent(thisParent, UserEditAdminActivity::class.java)
            intent.putExtra("nip", v.tvNipUserDetail.text.toString())
            startActivity(intent)
        }

        val data = arguments
        v.tvNipUserDetail.setText(data?.get("nip").toString())
        terimaJabatan = data?.get("jabatan").toString()

        if (terimaJabatan.equals("Admin")) {
            v.btnEditUserDetail.visibility = View.VISIBLE
        } else {
            v.btnEditUserDetail.visibility = View.GONE
        }

        return v
    }

    override fun onStart() {
        super.onStart()
        showDataUser("detailUser")
    }

    private fun showDataUser(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlUser,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val nama = jsonObject.getString("nama")
                val jabatan = jsonObject.getString("jabatan")
                val golongan = jsonObject.getString("golongan")
                val noHp = jsonObject.getString("no_hp")
                val masaKerja = jsonObject.getString("masa_kerja")
                val statusAkun = jsonObject.getString("sts_akun")
                val imgProfil = jsonObject.getString("foto")

                if (jabatan.equals("null") || golongan.equals("null") || noHp.equals("null") || masaKerja.equals("null")) {
                    v.tvNamaUserDetail.setText(nama)
                    v.tvJabatanUserDetail.setText("default")
                    v.tvGolonganUserDetail.setText("default")
                    v.tvNoHpUserDetail.setText("default")
                    v.tvMasaKerjaUserDetail.setText("default")
                } else {
                    v.tvNamaUserDetail.setText(nama)
                    v.tvJabatanUserDetail.setText(jabatan)
                    v.tvGolonganUserDetail.setText(golongan)
                    v.tvNoHpUserDetail.setText(noHp)
                    v.tvMasaKerjaUserDetail.setText(masaKerja+" Tahun")
                }

                if (masaKerja.equals("")) {
                    v.tvMasaKerjaUserDetail.setText("")
                }

                if (statusAkun.equals("AKTIF")) {
                    v.tvStsAkunUserDetail.setTextColor(Color.GREEN)
                    v.tvStsAkunUserDetail.setText("Aktif")
                } else {
                    v.tvStsAkunUserDetail.setTextColor(Color.RED)
                    v.tvStsAkunUserDetail.setText("NonAktif")
                }

                if (imgProfil.equals("null")){
                    Picasso.get().load(R.drawable.akun).into(v.imgUserDetail)
                } else {
                    Picasso.get().load(imgProfil).into(v.imgUserDetail)
                }
            },
            Response.ErrorListener { error ->
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                when(mode){
                    "detailUser" -> {
                        hm.put("mode","detailUser")
                        hm.put("nip", v.tvNipUserDetail.text.toString())
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(thisParent)
        queue.add(request)
    }
}