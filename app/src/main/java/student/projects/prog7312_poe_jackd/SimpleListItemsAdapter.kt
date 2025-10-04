package student.projects.prog7312_poe_jackd

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import student.projects.prog7312_poe_jackd.R

class SimpleListItemsAdapter(
    private val items: MutableList<String>
) : RecyclerView.Adapter<SimpleListItemsAdapter.ListItemViewHolder>() {

    class ListItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemText: TextView = view.findViewById(R.id.list_item_text)
        val editBtn: ImageButton = view.findViewById(R.id.edit_button)
        val deleteBtn: ImageButton = view.findViewById(R.id.delete_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bullet_point, parent, false)
        return ListItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        val item = items[position]
        holder.itemText.text = "• $item"

        // ✏️ Edit item locally
        holder.editBtn.setOnClickListener {
            val input = EditText(holder.itemView.context)
            input.setText(item)

            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Edit Item")
                .setView(input)
                .setPositiveButton("Save") { _, _ ->
                    val newItem = input.text.toString().trim()
                    if (newItem.isNotEmpty()) {
                        items[position] = newItem
                        notifyItemChanged(position)
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // 🗑️ Delete item locally
        holder.deleteBtn.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Yes") { _, _ ->
                    items.removeAt(position)
                    notifyItemRemoved(position)
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    override fun getItemCount() = items.size
}
