package com.example.cleanwashlaundromat.ui.order

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.cleanwashlaundromat.R
import com.example.cleanwashlaundromat.data.model.DropOffOrderRequest
import com.example.cleanwashlaundromat.data.model.SelfServiceOrderRequest
import com.example.cleanwashlaundromat.databinding.ActivityOrderBinding
import com.example.cleanwashlaundromat.ui.beranda.BerandaActivity
import com.example.cleanwashlaundromat.ui.pembayaran.PembayaranActivity
import java.text.SimpleDateFormat
import java.util.*

class OrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderBinding
    private val viewModel: OrderViewModel by viewModels()
    private var isSelfService = true
    private var selectedMesinId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPickers()
        setupToggleButton()
        setupBottomNavigation()
        setupCalculationListeners()
        setupOrderButton()
        observeViewModel()

        viewModel.fetchMesinReady()
    }

    private fun observeViewModel() {
        viewModel.mesinList.observe(this) { mesinList ->
            val mesinDisplayList = mutableListOf("-- Pilih Mesin --")
            mesinDisplayList.addAll(mesinList.map { "${it.nama_mesin} - ${it.type}" })

            // PERBAIKAN: Menggunakan ArrayAdapter untuk Spinner
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mesinDisplayList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerMesin.adapter = adapter

            // PERBAIKAN: Menggunakan OnItemSelectedListener untuk Spinner
            binding.spinnerMesin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (position > 0) {
                        val selectedMachine = mesinList[position - 1]
                        selectedMesinId = selectedMachine.id
                        binding.etDurasi.setText(selectedMachine.durasi.toString())
                    } else {
                        selectedMesinId = null
                        binding.etDurasi.setText("")
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Tidak perlu melakukan apa-apa
                }
            }
        }

        viewModel.totalBiaya.observe(this) { total ->
            binding.etTotalBiaya.setText(total.toString())
        }

        viewModel.orderResult.observe(this) { result ->
            result.onSuccess { response ->
                Toast.makeText(this, response.message, Toast.LENGTH_LONG).show()
                val intent = Intent(this, PembayaranActivity::class.java).apply {
                    putExtra("PEMBAYARAN_ID", response.pembayaran.id)
                    putExtra("TOTAL_BIAYA", response.order.totalBiaya)
                }
                startActivity(intent)
                finish()
            }.onFailure {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupOrderButton() {
        binding.btnOrder.setOnClickListener {
            if (isSelfService) {
                submitSelfServiceOrder()
            } else {
                submitDropOffOrder()
            }
        }
    }

    private fun submitSelfServiceOrder() {
        val tanggalOrderApi = binding.etTanggalOrderSelf.tag as? String

        if (tanggalOrderApi.isNullOrEmpty() || binding.etJamOrderSelf.text.isNullOrEmpty() || selectedMesinId == null) {
            Toast.makeText(this, "Lengkapi Tanggal, Jam, dan Mesin", Toast.LENGTH_SHORT).show()
            return
        }

        val request = SelfServiceOrderRequest(
            mesinId = selectedMesinId!!,
            tanggalOrder = tanggalOrderApi,
            jamOrder = binding.etJamOrderSelf.text.toString(),
            durasi = binding.etDurasi.text.toString().toIntOrNull() ?: 0,
            koin = binding.etJumlahKoin.text.toString().toIntOrNull() ?: 0,
            catatan = binding.etCatatan.text.toString(),
            totalBiaya = binding.etTotalBiaya.text.toString().toLongOrNull() ?: 0
        )
        viewModel.submitSelfServiceOrder(request)
    }

    private fun submitDropOffOrder() {
        val tanggalOrderApi = binding.etTanggalOrderDrop.tag as? String
        val tanggalAmbilApi = binding.etTanggalAmbil.tag as? String

        if (tanggalOrderApi.isNullOrEmpty() || tanggalAmbilApi.isNullOrEmpty() || binding.etJamOrderDrop.text.isNullOrEmpty()) {
            Toast.makeText(this, "Lengkapi Tanggal dan Jam", Toast.LENGTH_SHORT).show()
            return
        }

        val request = DropOffOrderRequest(
            tanggalOrder = tanggalOrderApi,
            jamOrder = binding.etJamOrderDrop.text.toString(),
            berat = binding.etBerat.text.toString().toDoubleOrNull() ?: 0.0,
            detergent = binding.etJumlahDetergen.text.toString().toIntOrNull() ?: 0,
            tanggalAmbil = tanggalAmbilApi,
            catatan = binding.etCatatan.text.toString(),
            totalBiaya = binding.etTotalBiaya.text.toString().toLongOrNull() ?: 0
        )
        viewModel.submitDropOffOrder(request)
    }

    private fun setupPickers() {
        binding.etTanggalOrderSelf.setOnClickListener { showDatePickerDialog(it as EditText, true) }
        binding.etJamOrderSelf.setOnClickListener { showTimePickerDialog(it as EditText) }
        binding.etTanggalOrderDrop.setOnClickListener { showDatePickerDialog(it as EditText, true) }
        binding.etJamOrderDrop.setOnClickListener { showTimePickerDialog(it as EditText) }
        binding.etTanggalAmbil.setOnClickListener { showDatePickerDialog(it as EditText, false) }
    }

    private fun setupToggleButton() {
        binding.toggleButtonGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                isSelfService = checkedId == R.id.btn_self_service
                binding.formSelfService.visibility = if (isSelfService) android.view.View.VISIBLE else android.view.View.GONE
                binding.formDropOff.visibility = if (isSelfService) android.view.View.GONE else android.view.View.VISIBLE
                triggerCalculation()
            }
        }
    }

    private fun setupCalculationListeners() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                triggerCalculation()
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        binding.etJumlahKoin.addTextChangedListener(textWatcher)
        binding.etBerat.addTextChangedListener(textWatcher)
        binding.etJumlahDetergen.addTextChangedListener(textWatcher)
    }

    private fun triggerCalculation() {
        if (isSelfService) {
            val koin = binding.etJumlahKoin.text.toString().toIntOrNull() ?: 0
            viewModel.calculateSelfServiceTotal(koin)
        } else {
            val berat = binding.etBerat.text.toString().toDoubleOrNull() ?: 0.0
            val detergen = binding.etJumlahDetergen.text.toString().toIntOrNull() ?: 0
            viewModel.calculateDropOffTotal(berat, detergen)
        }
    }

    private fun showDatePickerDialog(editText: EditText, isOrderDate: Boolean) {
        val calendar = Calendar.getInstance()
        val dialog = DatePickerDialog(this, { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)
            val apiFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            editText.setText(displayFormat.format(selectedDate.time))
            editText.tag = apiFormat.format(selectedDate.time)

            if (isOrderDate) {
                binding.etTanggalOrderSelf.setText(displayFormat.format(selectedDate.time))
                binding.etTanggalOrderDrop.setText(displayFormat.format(selectedDate.time))
                binding.etTanggalOrderSelf.tag = apiFormat.format(selectedDate.time)
                binding.etTanggalOrderDrop.tag = apiFormat.format(selectedDate.time)
            }

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        dialog.show()
    }

    private fun showTimePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(this, { _, hourOfDay, minute ->
            val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
            editText.setText(selectedTime)
            binding.etJamOrderSelf.setText(selectedTime)
            binding.etJamOrderDrop.setText(selectedTime)
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_order
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_beranda -> {
                    startActivity(Intent(this, BerandaActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_order -> true
                // ... navigasi lain
                else -> false
            }
        }
    }
}