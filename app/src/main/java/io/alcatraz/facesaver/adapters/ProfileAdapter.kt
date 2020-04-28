package io.alcatraz.facesaver.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.alcatraz.facesaver.R
import io.alcatraz.facesaver.core.ApplicationProfile
import io.alcatraz.facesaver.utils.PackageCtlUtils

class ProfileAdapter(
    private val context: Context,
    private val data: MutableList<ApplicationProfile>
) : RecyclerView.Adapter<ProfileHolder>(){
    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileHolder {
        val root = inflater.inflate(R.layout.item_profile, parent, false)
        return ProfileHolder(root)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ProfileHolder, position: Int) {
        val profile = data[position]
        val packIcon = PackageCtlUtils.getIcon(context,profile.pack)
        if(packIcon!=null){
            holder.packIcon.setImageDrawable(packIcon)
        }

        holder.txvLabel.text = PackageCtlUtils.getLabel(context,profile.pack)
        if (profile.doPauseMusicBroadcast || profile.doSetStreamVolumeZero){
            holder.txvMute.text = context.getString(R.string.ad_pb2)
        }else{
            holder.txvMute.text = context.getString(R.string.ad_nb2)
        }

        if(profile.backClickTimes > 0){
            holder.txvExit.text = context.getString(R.string.ad_pb2)
        }else{
            holder.txvExit.text = context.getString(R.string.ad_nb2)
        }

        holder.txvBackTimes.text = profile.backClickTimes.toString()
    }

}

class ProfileHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var packIcon: ImageView = itemView.findViewById(R.id.item_profile_app_icon)
    var txvLabel: TextView = itemView.findViewById(R.id.item_profile_app_name)
    var txvMute: TextView = itemView.findViewById(R.id.item_profile_mute)
    var txvExit: TextView = itemView.findViewById(R.id.item_profile_try_exit)
    var txvBackTimes: TextView = itemView.findViewById(R.id.item_profile_back_times)
}