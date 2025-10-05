package student.projects.prog7312_poe_jackd

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// ForecastAdapter takes a list of ForecastItem objects
class ForecastAdapter(private val items: List<ForecastItem>) :
    RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    class ForecastViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val txtDate: TextView = view.findViewById(R.id.txtDate)
        val txtTemp: TextView = view.findViewById(R.id.txtTemp)
        val txtHumidity: TextView = view.findViewById(R.id.txtHumidity)
        val txtWind: TextView = view.findViewById(R.id.txtWind)
        val txtCondition: TextView = view.findViewById(R.id.txtCondition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_forecast_card, parent, false)
        return ForecastViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val item = items[position]

        // Safely handle list values
        holder.txtDate.text = item.dt_txt.take(10) // yyyy-mm-dd
        holder.txtTemp.text = "🌡 ${item.main.temp} °C"
        holder.txtHumidity.text = "💧 ${item.main.humidity}%"
        holder.txtWind.text = "🌬 ${item.wind.speed} m/s"
        holder.txtCondition.text =
            "☁ ${item.weather.getOrNull(0)?.description?.replaceFirstChar { it.uppercase() } ?: "-"}"
    }
}
