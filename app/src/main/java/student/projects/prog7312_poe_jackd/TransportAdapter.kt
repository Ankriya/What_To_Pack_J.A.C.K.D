package student.projects.prog7312_poe_jackd

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransportAdapter : RecyclerView.Adapter<TransportAdapter.TransportViewHolder>() {

    private var items: List<Feature> = emptyList()  // start with empty list

    // Method to update the list
    fun setItems(newItems: List<Feature>) {
        items = newItems
        notifyDataSetChanged()
    }

    class TransportViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.nameText)
        val addressText: TextView = view.findViewById(R.id.addressText)
        val iconText: TextView = view.findViewById(R.id.iconText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransportViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transport, parent, false)
        return TransportViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransportViewHolder, position: Int) {
        val feature = items[position]
        holder.nameText.text = feature.properties.name ?: "Unknown"
        holder.addressText.text = feature.properties.address_line2 ?: "No address"

        // Determine icon more reliably
        val name = feature.properties.name?.lowercase() ?: ""
        val categoriesList = feature.properties.categories ?: emptyList<String>()

        val icon = when {
            categoriesList.any { it.contains("airport", ignoreCase = true) } ||
                    name.contains("airport") -> "✈️"

            categoriesList.any { it.contains("train", ignoreCase = true) } ||
                    name.contains("train") || name.contains("station") -> "🚆"

            categoriesList.any { it.contains("bus", ignoreCase = true) } ||
                    name.contains("bus") || name.contains("public transport") -> "🚌"

            else -> "🚏"
        }

        holder.iconText.text = icon
        holder.iconText.textSize = 40f
    }


    override fun getItemCount(): Int = items.size
}
