package com.example.cleanwashlaundromat

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class BerandaActivity : AppCompatActivity() {

    lateinit var prefs: SharedPreferences
    lateinit var txtNamaUser: TextView
    lateinit var imgProfile: ImageView
    lateinit var recyclerMesin: RecyclerView
    val listMesin = mutableListOf<Mesin>()
    lateinit var adapter: MesinAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beranda)

        prefs = getSharedPreferences("MY_APP", MODE_PRIVATE)

        txtNamaUser = findViewById(R.id.txtNamaUser)
        imgProfile = findViewById(R.id.imgProfile)
        recyclerMesin = findViewById(R.id.recyclerMesin)

        adapter = MesinAdapter(listMesin)
        recyclerMesin.layoutManager = LinearLayoutManager(this)
        recyclerMesin.adapter = adapter

        val token = prefs.getString("TOKEN", "") ?: ""
        loadBeranda(token)
    }

    private fun loadBeranda(token: String) {
        val request = Request.Builder()
            .url("http://10.0.2.2:8000/api/admin/beranda")
            .header("Authorization", "Bearer $token")
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@BerandaActivity, "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body()?.string()
                if (response.isSuccessful) {
                    val json = JSONObject(body)
                    val nama = json.getString("nama")
                    val foto = json.getString("foto")

                    val mesinArray = json.getJSONArray("mesin")
                    listMesin.clear()
                    for (i in 0 until mesinArray.length()) {
                        val m = mesinArray.getJSONObject(i)
                        listMesin.add(
                            Mesin(m.getString("nama"), m.getString("tipe"), m.getString("status"))
                        )
                    }

                    runOnUiThread {
                        txtNamaUser.text = "Hi, $nama"
                        Glide.with(this@BerandaActivity)
                            .load(foto)
                            .circleCrop()
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .into(imgProfile)

                        adapter.notifyDataSetChanged()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@BerandaActivity, "Gagal ambil data", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
