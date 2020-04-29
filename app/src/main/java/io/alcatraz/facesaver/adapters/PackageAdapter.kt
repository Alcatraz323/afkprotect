package io.alcatraz.facesaver.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.alcatraz.facesaver.R
import io.alcatraz.facesaver.activities.ProfileConfigureActivity
import io.alcatraz.facesaver.extended.CompatWithPipeActivity
import io.alcatraz.facesaver.utils.PackageCtlUtils
import java.util.*
import kotlin.collections.ArrayList


class PackageAdapter(
    private val activity: CompatWithPipeActivity,
    private val data: MutableList<PackageInfo>
) : RecyclerView.Adapter<PackHolder>() {
    private val inflater: LayoutInflater =
        activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private lateinit var filter: PackageFilter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackHolder {
        val root = inflater.inflate(R.layout.item_app_pick, parent, false)
        return PackHolder(root)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: PackHolder, position: Int) {
        val profile = data[position]
        val iconDrawable = PackageCtlUtils.getIcon(activity, profile.applicationInfo)
        if (iconDrawable != null) {
            holder.packIcon.setImageDrawable(iconDrawable)
        }

        holder.txvPack.text = profile.packageName
        holder.txvLabel.text = PackageCtlUtils.getLabel(activity, profile.applicationInfo)
        holder.itemView.setOnClickListener {
            val data = Intent()
            data.putExtra(
                ProfileConfigureActivity.ACTION_PACK_PICK,
                profile.packageName
            )
            activity.setResult(Activity.RESULT_OK, data)
            activity.finish()
        }
    }

    private fun getFilter(): Filter {
        return filter
    }

    fun onTextChanged(newtext: String?) {
        getFilter().filter(newtext)
    }

    private inner class PackageFilter(tofilter: MutableList<PackageInfo>) :
        Filter() {
        var original: List<PackageInfo> = tofilter
        override fun performFiltering(charSequence: CharSequence?): FilterResults {
            val results = FilterResults()
            if (charSequence == null || charSequence.isEmpty()) {
                results.values = original
                results.count = original.size
            } else {
                val mList: MutableList<PackageInfo> = ArrayList()
                for (info in original) {
                    if (PackageCtlUtils.getLabel(activity,info.applicationInfo).toLowerCase(Locale.getDefault())
                            .contains(charSequence.toString().toLowerCase(Locale.getDefault()))
                        || info.packageName.toLowerCase(Locale.getDefault())
                            .contains(charSequence.toString().toLowerCase(Locale.getDefault()))
                    ) {
                        mList.add(info)
                    }
                }
                results.values = mList
                results.count = mList.size
            }
            return results
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(
            charSequence: CharSequence?,
            filterResults: FilterResults
        ) {
            data.clear()
            data.addAll(filterResults.values as MutableList<PackageInfo>)
            notifyDataSetChanged()
        }

    }
}

class PackHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var packIcon: ImageView = itemView.findViewById(R.id.app_pick_pack_icon)
    var txvLabel: TextView = itemView.findViewById(R.id.app_pick_pack_label)
    var txvPack: TextView = itemView.findViewById(R.id.app_pick_pack_name)
}