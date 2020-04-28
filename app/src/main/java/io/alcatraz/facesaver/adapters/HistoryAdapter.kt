package io.alcatraz.facesaver.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.alcatraz.facesaver.R
import io.alcatraz.facesaver.activities.RecordActivity
import io.alcatraz.facesaver.core.Record
import io.alcatraz.facesaver.extended.CompatWithPipeActivity

class HistoryAdapter(
    private val activity: CompatWithPipeActivity,
    private val data: List<Record>
) : RecyclerView.Adapter<HistoryHolder>() {
    private val inflater: LayoutInflater =
        activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryHolder {
        val root = inflater.inflate(R.layout.item_history, parent, false)
        return HistoryHolder(root)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HistoryHolder, position: Int) {
        val record = data[position]
        holder.txvStartTime.text = Record.convertTime(record.startTime)
        holder.txvElapsedTime.text =
            ((record.endTime - record.startTime) / 1000 / 3600).toString() + "h"
        holder.txvScreenOnTimes.text = record.screenOnTimes.toString()
        holder.itemView.setOnClickListener {
            val intent = Intent(activity, RecordActivity::class.java)
            intent.putExtra(RecordActivity.KEY_RECORD_DATA, record)
            activity.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}

class HistoryHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var txvStartTime: TextView = itemView.findViewById(R.id.item_history_start_time)
    var txvElapsedTime: TextView = itemView.findViewById(R.id.item_history_elapsed_time)
    var txvScreenOnTimes: TextView = itemView.findViewById(R.id.item_history_screen_on_times)
}