package io.alcatraz.afkprotect.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.alcatraz.afkprotect.R
import io.alcatraz.afkprotect.activities.ProfileConfigureActivity
import io.alcatraz.afkprotect.core.AccessibilityEventTaskManager
import io.alcatraz.afkprotect.extended.CompatWithPipeActivity
import io.alcatraz.afkprotect.utils.PackageCtlUtils
import java.util.*


class PackageAdapter(
    private val activity: CompatWithPipeActivity,
    private val data: MutableList<PackageInfo>,
    private val isForIgnorePick: Boolean,
    private val manager: AccessibilityEventTaskManager
) : RecyclerView.Adapter<PackHolder>() {
    private val inflater: LayoutInflater =
        activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var filter: PackageFilter? = null


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
        if (isForIgnorePick) {
            holder.checkBox.visibility = View.VISIBLE
            if (manager.ignoreContains(profile.packageName)) {
                holder.checkBox.isChecked = true
            }
            holder.itemView.setOnClickListener {
                if (holder.checkBox.isChecked) {
                    manager.ignoreRemove(profile.packageName)
                } else {
                    manager.ignoreAdd(profile.packageName)
                }
                holder.checkBox.isChecked = !holder.checkBox.isChecked
            }
        } else {
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
    }

    private fun getFilter(): PackageFilter {
        if(filter == null){
            filter = PackageFilter(data)
        }
        return filter!!
    }

    fun onTextChanged(newText: String?) {
        getFilter().filter(newText)
    }

    private inner class PackageFilter(val original: MutableList<PackageInfo>) :
        Filter() {
        val copyList = mutableListOf<PackageInfo>()
        init {
            copyList.addAll(original)
        }
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val results = FilterResults()
            if (p0 == null || p0.isEmpty()) {
                results.values = copyList
                results.count = copyList.size
            } else {
                val mList: MutableList<PackageInfo> = mutableListOf()
                for (info in copyList) {
                    if (PackageCtlUtils.getLabel(activity, info.applicationInfo)
                            .toLowerCase(Locale.getDefault())
                            .contains(p0.toString().toLowerCase(Locale.getDefault()))
                        || info.packageName.toLowerCase(Locale.getDefault())
                            .contains(p0.toString().toLowerCase(Locale.getDefault()))
                    ) {
                        mList.add(info)
                    }
                }
                results.values = mList
                results.count = mList.size
            }
            return results
        }

        override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
            data.clear()
            data.addAll(p1?.values as MutableList<PackageInfo>)
            notifyDataSetChanged()
        }
    }
}

class PackHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    val packIcon: ImageView = itemView.findViewById(R.id.app_pick_pack_icon)
    val txvLabel: TextView = itemView.findViewById(R.id.app_pick_pack_label)
    val txvPack: TextView = itemView.findViewById(R.id.app_pick_pack_name)
    val checkBox: CheckBox = itemView.findViewById(R.id.app_pick_pack_checkbox)
}