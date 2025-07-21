package com.example.cleanwashlaundromat

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    lateinit var prefs: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        prefs = getSharedPreferences("MY_APP", MODE_PRIVATE)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            login(email, password)
        }
    }

    private fun login(email: String, password: String) {
        val json = JSONObject()
        json.put("email", email)
        json.put("password", password)

        val body = RequestBody.create(
            MediaType.parse("application/json"),
            json.toString()
        )

        val request = Request.Builder()
            .url("http://10.0.2.2:8000/api/admin/login")
            .post(body)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body()?.string()
                val jsonResp = JSONObject(responseBody)
                if (response.isSuccessful) {
                    val token = jsonResp.getString("token")
                    prefs.edit().putString("TOKEN", token).apply()
                    startActivity(Intent(this@LoginActivity, BerandaActivity::class.java))
                    finish()
                } else {
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Login gagal", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
