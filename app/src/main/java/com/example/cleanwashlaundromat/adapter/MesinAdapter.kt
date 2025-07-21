import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MesinAdapter(private val list: List<Mesin>) :
    RecyclerView.Adapter<MesinAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nama = view.findViewById<TextView>(android.R.id.text1)
        val detail = view.findViewById<TextView>(android.R.id.text2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val m = list[position]
        holder.nama.text = m.nama
        holder.detail.text = "${m.tipe} - ${m.status}"
    }

    override fun getItemCount(): Int = list.size
}
