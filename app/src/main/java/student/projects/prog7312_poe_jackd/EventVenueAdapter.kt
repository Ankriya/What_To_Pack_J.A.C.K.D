package student.projects.prog7312_poe_jackd

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EventVenueAdapter(private var venues: List<Properties>) :
    RecyclerView.Adapter<EventVenueAdapter.VenueViewHolder>() {

    inner class VenueViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val venueName: TextView = view.findViewById(R.id.textView)
        val venueLocation: TextView = view.findViewById(R.id.textView2)
        val venueType: TextView = view.findViewById(R.id.textView3)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VenueViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_events, parent, false)
        return VenueViewHolder(view)
    }

    override fun onBindViewHolder(holder: VenueViewHolder, position: Int) {
        val venue = venues[position]
        holder.venueName.text = "Venue: ${venue.name ?: "Unknown"}"
        holder.venueLocation.text = "Location: ${venue.address_line2 ?: "N/A"}"
        holder.venueType.text = "Type: ${venue.datasource?.raw?.amenity ?: venue.categories?.firstOrNull() ?: "Unknown"}"
    }

    override fun getItemCount() = venues.size

    fun updateData(newVenues: List<Properties>) {
        venues = newVenues
        notifyDataSetChanged()
    }
}
