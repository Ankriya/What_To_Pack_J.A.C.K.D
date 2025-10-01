package student.projects.prog7312_poe_jackd

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TripsAdapter(private val trips: List<Trip>) : RecyclerView.Adapter<TripsAdapter.TripViewHolder>() {

    class TripViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fromText: TextView = view.findViewById(R.id.fromText)
        val toText: TextView = view.findViewById(R.id.toText)
        val timeText: TextView = view.findViewById(R.id.timeText)
        val airportText: TextView = view.findViewById(R.id.airportText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trip, parent, false)
        return TripViewHolder(view)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip = trips[position]
        holder.fromText.text = "FROM: ${trip.fromLocation}"
        holder.toText.text = "TO: ${trip.toLocation}"
        holder.timeText.text = "TIME: ${trip.time}"
        holder.airportText.text = "AIRPORT: ${trip.airport}"
    }

    override fun getItemCount() = trips.size
}