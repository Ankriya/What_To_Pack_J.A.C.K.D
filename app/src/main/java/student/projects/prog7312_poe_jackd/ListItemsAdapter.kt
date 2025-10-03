package student.projects.prog7312_poe_jackd

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListItemsAdapter(private val items: List<String>) : RecyclerView.Adapter<ListItemsAdapter.ListItemViewHolder>() {

    class ListItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemText: TextView = view.findViewById(R.id.list_item_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bullet_point, parent, false)
        return ListItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        val item = items[position]
        holder.itemText.text = "• $item"
    }

    override fun getItemCount() = items.size
}
