package com.example.cleanwashlaundromat.ui.beranda

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.cleanwashlaundromat.R
import com.example.cleanwashlaundromat.data.model.Mesin

class MesinAdapter(private var mesinList: List<Mesin>) : RecyclerView.Adapter<MesinAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nama: TextView = view.findViewById(R.id.tvNamaMesin)
        val tipe: TextView = view.findViewById(R.id.tvTipeMesin)
        val status: TextView = view.findViewById(R.id.tvStatusMesin)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mesin, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mesin = mesinList[position]
        holder.nama.text = mesin.nama_mesin
        holder.tipe.text = "Tipe : ${mesin.tipe}"
        holder.status.text = mesin.status
        val statusColor = if (mesin.status.equals("Ready", ignoreCase = true)) {
            R.color.green_ready
        } else {
            R.color.red_not_ready
        }
        holder.status.background.setTint(ContextCompat.getColor(holder.itemView.context, statusColor))
    }
    override fun getItemCount() = mesinList.size
}