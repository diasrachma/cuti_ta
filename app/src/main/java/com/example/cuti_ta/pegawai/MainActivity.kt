package com.example.cuti_ta.pegawai

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.cuti_ta.BaseApplication
import com.example.cuti_ta.LoginActivity
import com.example.cuti_ta.PrivasiAkunActivity
import com.example.cuti_ta.R
import com.example.cuti_ta.UrlClass
import com.example.cuti_ta.adapter.AdapterIzinArsip
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.*
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle

    lateinit var urlClass: UrlClass
    lateinit var preferences: SharedPreferences

    val daftarIzin = mutableListOf<HashMap<String,String>>()
    lateinit var izinAdapter : AdapterIzinArsip

    val PREF_NAME = "akun"
    val USERNAME = "username"
    val DEF_USERNAME = ""
    val PASSWORD = "password"
    val DEF_PASSWORD = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.setTitle("Home Pegawai")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        urlClass = UrlClass()
        preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        izinAdapter = AdapterIzinArsip(daftarIzin)
        rvIzinKeluarMain.layoutManager = LinearLayoutManager(this)
        rvIzinKeluarMain.adapter = izinAdapter

        val drawerLayout : DrawerLayout = findViewById(R.id.drawerLayout)
        val navView : NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.nav_homePegawai -> {
                    supportActionBar?.setTitle("Home Pegawai")
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.nav_profilPegawai -> {
                    startActivity(Intent(this, ProfilPegawaiActivity::class.java))
                }
                R.id.nav_formPegawai -> {
                    startActivity(Intent(this, FormPegawaiActivity::class.java))
                }
                R.id.nav_riwayatPegawai -> {
                    startActivity(Intent(this, RiwayatPegawaiActivity::class.java))
                }
                R.id.nav_kuotaPegawai -> {
                    startActivity(Intent(this, KuotaCutiPegawaiActivity::class.java))
                }
                R.id.nav_formIzinKeluarPegawai -> {
                    startActivity(Intent(this, IzinFormPegawaiActivity::class.java))
                }
                R.id.nav_riwayatIzinKeluarPegawai -> {
                    startActivity(Intent(this, IzinRiwayatPegawaiActivity::class.java))
                }
                R.id.nav_resetPegawai -> {
                    startActivity(Intent(this, PrivasiAkunActivity::class.java))
                }
                R.id.nav_informasiPegawai -> {
                    startActivity(Intent(this, WhatsAppActivity::class.java))
                }
                R.id.nav_logoutPegawai -> {
                    AlertDialog.Builder(this)
                        .setIcon(R.drawable.warning)
                        .setTitle("Logout")
                        .setMessage("Apakah Anda ingin keluar aplikasi?")
                        .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                            val prefEditor = preferences.edit()
                            prefEditor.putString(USERNAME,null)
                            prefEditor.putString(PASSWORD,null)
                            prefEditor.commit()

                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finishAffinity()
                        })
                        .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->
                        })
                        .show()
                    true
                }
            }
//            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        showChart("chart_pegawai")
        izinTerlambat()
    }

    private fun showChart(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlChart,
            Response.Listener { response ->
                val entries = ArrayList<PieEntry>()
                val jsonArray = JSONArray(response)
                for (i in 0..(jsonArray.length()-1)){
                    val jsonObject = jsonArray.getJSONObject(i)
                    val value = jsonObject.getInt("value")
                    val sts = jsonObject.getString("status_cuti")
                    entries.add(PieEntry(value.toFloat(), sts))
                }

                setupPieChart(entries)
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                when(mode){
                    "chart_pegawai" -> {
                        hm.put("mode","chart_pegawai")
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

    private fun setupPieChart(entries: ArrayList<PieEntry>) {
        val dataSet = PieDataSet(entries, "Data")
        dataSet.setColors(Color.parseColor("#FF9CCC65"), Color.parseColor("#FFEF5350"), Color.parseColor("#FF42A5F5")) // Atur warna slice
        dataSet.setDrawValues(true)
        dataSet.valueFormatter = DefaultValueFormatter(0)

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.description.isEnabled = false // Nonaktifkan deskripsi
        pieChart.setUsePercentValues(false)
        pieChart.legend.isEnabled = false
        pieChart.animateY(1000)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun WhatsApp() {
        try {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Hello Admin")
                putExtra("jid", "${+6285748053370}@s.whatsapp.net")
                type = "text/plain"
                setPackage("com.whatsapp")
            }
            startActivity(sendIntent)
        }catch (e: Exception){
            e.printStackTrace()
            val appPackageName = "com.whatsapp"
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
            } catch (e :android.content.ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        showProfil("show_profil")
        showKuotaCuti("readDetailKuota")
        showDataIzin("read_arsip")
    }

    fun izinTerlambat() {
        val request = object : StringRequest(
            Method.POST,urlClass.urlIzin,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val respon = jsonObject.getString("respon")
                val nama = jsonObject.getString("nama")
                val id = jsonObject.getString("id_izinkeluar")

                if (respon.equals("1")) {
                    BaseApplication.notificationHelper.notifTelatKembali(nama,
                        "Terdapat izin keluar yang melebihi jam kembali. Segera konfirmasi jika Anda telah kembali ke kantor!", id)
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                hm.put("username",preferences.getString(USERNAME, DEF_USERNAME).toString())
                hm.put("password",preferences.getString(PASSWORD, DEF_PASSWORD).toString())
                hm.put("mode", "notif_izin_terlambat")

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    private fun showDataIzin(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlIzin,
            Response.Listener { response ->
                daftarIzin.clear()
                if (response.equals(0)) {
                    Toast.makeText(this,"Data tidak ditemukan", Toast.LENGTH_LONG).show()
                } else {
                    val jsonArray = JSONArray(response)
                    for (x in 0..(jsonArray.length()-1)){
                        val jsonObject = jsonArray.getJSONObject(x)
                        var  frm = HashMap<String,String>()
                        frm.put("img_profil",jsonObject.getString("img_profil"))
                        frm.put("nama",jsonObject.getString("nama"))
                        frm.put("id_izinkeluar",jsonObject.getString("id_izinkeluar"))
                        frm.put("tgl_izinkeluar",jsonObject.getString("tgl_izinkeluar"))
                        frm.put("jam_izinkeluar",jsonObject.getString("jam_izinkeluar"))

                        daftarIzin.add(frm)
                    }
                    izinAdapter.notifyDataSetChanged()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                when(mode){
                    "read_arsip" -> {
                        hm.put("mode","read_arsip_pimpinan_main")
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    private fun showProfil(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlProfil,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val nama = jsonObject.getString("nama")
                val jabatan = jsonObject.getString("jabatan")
                val ket = jsonObject.getString("keterangan")
                val foto = jsonObject.getString("foto")

                ketHeader.setText(ket)
                usernameHeader.setText(nama)
                jabatanHeader.setText(jabatan)
                Picasso.get().load(foto).into(profilHeader)
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
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
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    private fun showKuotaCuti(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlSisa,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val sisaCuti = jsonObject.getString("sisa_kuota")
                val terpakai = jsonObject.getString("terpakai")

                tvTerpakaiKuotaMain.setText(terpakai + " Hari")
                tvCutiKuotaMain.setText(sisaCuti + " Hari")
            },
            Response.ErrorListener { error ->
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = java.util.HashMap<String, String>()
                hm.put("username",preferences.getString(USERNAME, DEF_USERNAME).toString())
                hm.put("password",preferences.getString(PASSWORD, DEF_PASSWORD).toString())
                when(mode){
                    "readDetailKuota" -> {
                        hm.put("mode","readDetailKuota")
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    class AdapterArsip(val dataArsip: List<HashMap<String,String>>) : RecyclerView.Adapter<AdapterArsip.HolderDataArsip>() {
        class HolderDataArsip(v : View) : RecyclerView.ViewHolder(v) {
            val image_cuti = v.findViewById<ImageView>(R.id.imgCutiAdmin)
            val namaAdmin = v.findViewById<TextView>(R.id.tvNamaCutiAdmin)
            val jenis_cuti = v.findViewById<TextView>(R.id.tvJenisCutiAdmin)
            val tanggal_cuti = v.findViewById<TextView>(R.id.tvTanggalCutiAdmin)
            val icon_admin = v.findViewById<ImageView>(R.id.iconAdmin)
            val card = v.findViewById<CardView>(R.id.cardCutiAdmin)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataArsip {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.row_cuti_admin,parent,false)
            return HolderDataArsip(v)
        }

        override fun onBindViewHolder(holder: HolderDataArsip, position: Int) {
            val data = dataArsip.get(position)
            Picasso.get().load(data.get("foto")).into(holder.image_cuti)
            holder.namaAdmin.setText(data.get("nama"))
            holder.jenis_cuti.setText(data.get("jenis_cuti"))
            holder.tanggal_cuti.setText(data.get("tanggal_cuti"))

            val sts = data.get("status_cuti").toString()
            if (sts.equals("Diterima")) {
                holder.icon_admin.setBackgroundResource(R.drawable.success)
            } else if (sts.equals("Pending")) {
                holder.icon_admin.setBackgroundResource(R.drawable.information)
            } else {
                holder.icon_admin.setBackgroundResource(R.drawable.cancel)
            }

            holder.card.setOnClickListener{ v : View ->
                val intentDetail = Intent(v.context, RiwayatDetailPegawaiActivity::class.java)
                intentDetail.putExtra("idCuti",data.get("id_cuti"))
                v.context.startActivity(intentDetail)
            }
        }

        override fun getItemCount(): Int {
            return dataArsip.size
        }
    }
}